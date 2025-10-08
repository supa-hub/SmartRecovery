package com.kunto.smartrecovery

import okio.Path

interface Platform {
    val name: String
    fun isAndroid(): Boolean
    fun isIOS(): Boolean
    fun cwd(): Path
    fun log(text: String): Unit
    fun context(): Any
}

expect fun getPlatform(): Platform