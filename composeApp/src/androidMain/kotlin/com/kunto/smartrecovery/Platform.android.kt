package com.kunto.smartrecovery

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.Path
import okio.Path.Companion.toPath


object Constants
{
    lateinit var baseDir: Path
}

class AndroidPlatform : Platform {
    private val baseDir: Path = Constants.baseDir
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun isAndroid(): Boolean = true
    override fun isIOS(): Boolean = false
    override fun cwd(): Path = baseDir
}

actual fun getPlatform(): Platform = AndroidPlatform()