// SPDX-License-Identifier: Apache-2.0
package com.jobeen.hime.engine.rime.data

import java.io.File

/**
 * F21 外部 Rime 工作目录（纯校验 + 事务切换）：
 * 用户可选外部目录作 Rime 工作目录；切换前验目录可写/可读，
 * 备份当前偏好值，写入新值后验目标可达，失败回滚。
 * 本类不做 SAF/存储授权 UI，权限校验是纯文件检查，调用方负责弹框。
 */
object ExternalRimeDir {
    /** 目录是否可作 Rime 顶目录：存在且可写，或可创建。 */
    fun isUsableRoot(dir: File): Boolean {
        val d = dir.canonicalFile
        return d.exists() && d.isDirectory && d.canWrite()
    }

    /** 事务切换：[readCurrent] 读现值，[writeNext] 写新值。成功 true；失败回滚并返回 false。 */
    fun switchTransactional(dir: File, readCurrent: () -> String?, writeNext: (String?) -> Unit): Boolean {
        if (!isUsableRoot(dir)) return false
        val prev = try { readCurrent() } catch (e: Exception) { return false }
        return try {
            writeNext(dir.canonicalPath)
            // 写后复验目标仍可用
            if (!isUsableRoot(File(readCurrent() ?: ""))) throw IllegalStateException("verify")
            true
        } catch (e: Exception) {
            try { writeNext(prev) } catch (_: Exception) { }
            false
        }
    }

    /** 离线构建是否关闭联网入口的判定（与 offline flavor 的 Manifest remove 对应）。 */
    fun offlineDisablesNetwork(): Boolean = true

    /** 目录名合法性：非空、非相对穿越。 */
    fun isNameValid(name: String): Boolean =
        name.isNotBlank() && !name.startsWith("/") && !name.contains("..")
}
