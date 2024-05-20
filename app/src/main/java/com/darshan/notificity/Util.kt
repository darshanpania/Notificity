package com.darshan.notificity

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

open class Util {
    companion object {
        fun convertEpochLongToString(epochLong: Long): String {
            val instant = Instant.ofEpochMilli(epochLong)
            val zoneId = ZoneId.systemDefault()
            val localDate = instant.atZone(zoneId).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")
            return localDate.format(formatter)
        }
    }

}