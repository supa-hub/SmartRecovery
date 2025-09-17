package com.kunto.smartrecovery

import okio.Path

interface Platform {
    val name: String
    fun isAndroid(): Boolean
    fun isIOS(): Boolean
    fun cwd(): Path
}

expect fun getPlatform(): Platform