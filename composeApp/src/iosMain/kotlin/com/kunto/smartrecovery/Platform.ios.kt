package com.kunto.smartrecovery

import okio.Path
import platform.UIKit.UIDevice
import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.posix.getcwd
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSLog
import platform.Foundation.NSUserDomainMask
import platform.posix.fflush
import platform.posix.stderr
import kotlinx.datetime.DayOfWeek
import platform.Foundation.*


class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun isAndroid(): Boolean = false
    override fun isIOS(): Boolean = true

    @OptIn(ExperimentalForeignApi::class)
    override fun cwd(): Path = memScoped {
        //val temp = allocArray<ByteVar>(PATH_MAX + 1)
        //getcwd(temp, PATH_MAX.convert())
        //temp.toKString()
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        paths.first() as String
    }.toPath(normalize = true)

    @OptIn(ExperimentalForeignApi::class)
    override fun log(text: String) {
        NSLog(text)
    }
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun DayOfWeek.localizedName(): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = "EEEE" // full weekday name
        locale = NSLocale.currentLocale
    }

    // Create a date corresponding to this weekday (any week works)
    val calendar = NSCalendar.currentCalendar
    val components = NSDateComponents()
    components.weekday = this.ordinal.toLong() + 1 // Sunday=1 in iOS
    val date = calendar.dateFromComponents(components)!!

    return formatter.stringFromDate(date)
}