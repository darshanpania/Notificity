package com.darshan.notificity.utils

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

        fun getEpoch(): Long {
            return System.currentTimeMillis() / 1000
        }
    }
}