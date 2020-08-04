package io.genanik.picfinder.plugins.time

import java.text.SimpleDateFormat
import java.util.*

/**
 * 返回当前时间
 */
class Time {

    fun getNow(): String {
        return SimpleDateFormat("HH:mm").format(Date())
    }

}