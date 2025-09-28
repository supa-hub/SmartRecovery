package com.kunto.smartrecovery

import okio.Path

interface Platform {
    val name: String
    fun isAndroid(): Boolean
    fun isIOS(): Boolean
    fun cwd(): Path
    fun log(text: String): Unit
}

expect fun getPlatform(): Platform