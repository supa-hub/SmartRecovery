package com.kunto.smartrecovery

import kotlinx.datetime.DayOfWeek
import okio.Path

interface Platform {
    val name: String
    fun isAndroid(): Boolean
    fun isIOS(): Boolean
    fun cwd(): Path
    fun log(text: String): Unit
}

expect fun getPlatform(): Platform

expect fun DayOfWeek.localizedName(): String