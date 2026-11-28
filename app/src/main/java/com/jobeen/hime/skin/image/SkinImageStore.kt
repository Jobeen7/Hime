package com.jobeen.hime.skin.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jobeen.hime.data.App
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 皮肤图片资产纯逻辑（无 Android 依赖，主机单测直测）：
 * 资产 id 安全校验、降采样倍数、候选文件名生成。
 */
internal object SkinImageStoreLogic {

    const val MAX_ASSET_ID_LENGTH = 200

    /** 解码后的位图缓存总字节红线（8 MiB），超出按 LRU 淘汰。 */
    const val MAX_CACHE_BYTES = 8 * 1024 * 1024

    /** 无请求尺寸时的解码最长边上限，避免超大图打爆内存。 */
    const val MAX_DECODE_DIMENSION = 1024

    val IMAGE_EXTENSIONS: List<String> = listOf("png", "jpg", "jpeg", "webp", "gif")

    /** 资产 id 安全校验：拒绝绝对路径、穿越、空白与超长。 */
    fun isSafeAssetId(id: String): Boolean {
        if (id.isBlank() || id.length > MAX_ASSET_ID_LENGTH) return false
        if (id.startsWith("/") || id.contains("..")) return false
        if (id.any { it.isWhitespace() || it == '\\' }) return false
        return id.all { it.isLetterOrDigit() || it in "_-./" }
    }

    /** 是否已带图片扩展名。 */
    fun hasImageExtension(id: String): Boolean =
        IMAGE_EXTENSIONS.any { id.endsWith(".$it", ignoreCase = true) }

    /** 由资产 id 生成候选文件名列表（带扩展名时只返回自身）。 */
    fun candidateNames(id: String): List<String> =
        if (hasImageExtension(id)) {
            listOf(id)
        } else {
            IMAGE_EXTENSIONS.map { "$id.$it" }
        }

    /**
     * 降采样倍数（2 的幂）。reqW/reqH<=0 时按 [MAX_DECODE_DIMENSION] 封顶。
     */
    fun sampleSizeFor(srcW: Int, srcH: Int, reqW: Int, reqH: Int): Int {
        if (srcW <= 0 || srcH <= 0) return 1
        val w = if (reqW > 0) reqW else MAX_DECODE_DIMENSION
        val h = if (reqH > 0) reqH else MAX_DECODE_DIMENSION
        var size = 1
        while (srcW / (size * 2) >= w && srcH / (size * 2) >= h) {
            size *= 2
        }
        return size
    }

    /** 位图字节估算（宽*高*4）。 */
    fun bitmapBytes(width: Int, height: Int): Int = width * height * 4
}

/**
 * 皮肤图片资产供给（原生实现）：接回 v3 死壳删除后断掉的图片链。
 *
 * 供给优先级：皮肤库目录 skins/assets → 内置 assets（skin/native 优先）。
 * 解码在专用后台单线程执行，回调切回主线程；失败一律回 null，
 * 调用方以颜色键面回落。解码不在按键热路径同步执行。
 */
object SkinImageStore {

    private const val TAG = "SkinImageStore"

    private data class CacheKey(val assetId: String, val reqW: Int, val reqH: Int)

    private val lock = Any()
    private val cachedBytes = AtomicInteger(0)
    private val cache: LinkedHashMap<CacheKey, Bitmap> =
        object : LinkedHashMap<CacheKey, Bitmap>(16, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey, Bitmap>?): Boolean {
                if (cachedBytes.get() <= SkinImageStoreLogic.MAX_CACHE_BYTES) return false
                if (eldest != null) {
                    cachedBytes.addAndGet(
                        -SkinImageStoreLogic.bitmapBytes(eldest.value.width, eldest.value.height),
                    )
                }
                return true
            }
        }

    private val decoder = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "hime-skin-image")
    }
    private val mainHandler = Handler(Looper.getMainLooper())

    /** 同步取缓存（未命中返回 null）。 */
    fun peek(assetId: String, reqW: Int = 0, reqH: Int = 0): Bitmap? {
        if (!SkinImageStoreLogic.isSafeAssetId(assetId)) return null
        val key = CacheKey(assetId, reqW, reqH)
        synchronized(lock) { return cache[key] }
    }

    /**
     * 异步请求位图：命中缓存直接主线程回调，未命中后台解码后再回调。
     * 回调恒在主线程触发；失败回调 null。
     */
    fun request(
        context: Context,
        assetId: String,
        reqW: Int = 0,
        reqH: Int = 0,
        onBitmap: (Bitmap?) -> Unit,
    ) {
        if (!SkinImageStoreLogic.isSafeAssetId(assetId)) {
            onBitmap(null)
            return
        }
        peek(assetId, reqW, reqH)?.let { cached ->
            onBitmap(cached)
            return
        }
        val appContext = context.applicationContext
        decoder.execute {
            val bitmap = try {
                decode(appContext, assetId, reqW, reqH)
            } catch (t: Throwable) {
                Log.w(TAG, "decode failed for $assetId", t)
                null
            }
            if (bitmap != null) {
                synchronized(lock) {
                    val old = cache.put(CacheKey(assetId, reqW, reqH), bitmap)
                    cachedBytes.addAndGet(
                        SkinImageStoreLogic.bitmapBytes(bitmap.width, bitmap.height) -
                            (old?.let { SkinImageStoreLogic.bitmapBytes(it.width, it.height) } ?: 0),
                    )
                    // 触发淘汰检查
                    cache.entries
                }
            }
            mainHandler.post { onBitmap(bitmap) }
        }
    }

    /** 清缓存（主题切换/皮肤更换时调用）。 */
    fun clear() {
        synchronized(lock) {
            cache.clear()
            cachedBytes.set(0)
        }
    }

    private fun decode(context: Context, assetId: String, reqW: Int, reqH: Int): Bitmap? {
        openStream(context, assetId)?.use { stream ->
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            if (BitmapFactory.decodeStream(stream, null, bounds) == null) return null
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            val sample = SkinImageStoreLogic.sampleSizeFor(bounds.outWidth, bounds.outHeight, reqW, reqH)
            return openStream(context, assetId)?.use { again ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sample
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                BitmapFactory.decodeStream(again, null, options)
            }
        }
        return null
    }

    private fun openStream(context: Context, assetId: String): InputStream? {
        val names = SkinImageStoreLogic.candidateNames(assetId)
        // 1. 皮肤库目录（用户皮肤资产）
        val library = runCatching { App.skinLibraryDir }.getOrNull()
        if (library != null) {
            for (name in names) {
                for (dir in listOf("skins", "assets", "skins/assets")) {
                    val file = File(library, "$dir/$name")
                    if (file.isFile && file.length() > 0) {
                        runCatching { return FileInputStream(file) }
                    }
                }
            }
        }
        // 2. 内置资产（自带原生主题图，skin/native 优先）
        for (name in names) {
            for (prefix in listOf("skin/native", "skin", "")) {
                val path = if (prefix.isEmpty()) name else "$prefix/$name"
                runCatching { return context.assets.open(path) }
            }
        }
        return null
    }
}
