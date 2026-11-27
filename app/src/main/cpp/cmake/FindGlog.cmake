# SPDX-FileCopyrightText: 2026 Hime contributors
#
# SPDX-License-Identifier: BSD-3-Clause

set(Glog_FOUND TRUE)
set(Glog_LIBRARY glog)
get_target_property(Glog_INCLUDE_PATH glog::glog INTERFACE_INCLUDE_DIRECTORIES)
