package com.darshan.notificity.utils

import com.darshan.notificity.NotificationEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun List<NotificationEntity>.toCsvString(): String {
    val csvBuilder = StringBuilder()
    // Add header row
    csvBuilder.appendLine("id,notificationId,packageName,timestamp,appName,title,content,imageUrl,extras")

    // Add data rows
    this.forEach { entity ->
        csvBuilder.appendLine(
            listOfNotNull(
                entity.id.toString(),
                entity.notificationId.toString(),
                entity.packageName.escapeCsv(),
                entity.timestamp.toString(),
                entity.appName.escapeCsv(),
                entity.title.escapeCsv(),
                entity.content.escapeCsv(),
                entity.imageUrl?.escapeCsv() ?: "",
                entity.extras?.escapeCsv() ?: ""
            ).joinToString(",")
        )
    }
    return csvBuilder.toString()
}

fun List<NotificationEntity>.toJsonString(): String {
    return Json { prettyPrint = true }.encodeToString(this)
}

// Helper function to escape CSV special characters (quotes and commas)
private fun String.escapeCsv(): String {
    if (this.contains(",") || this.contains("\"") || this.contains("\n")) {
        return "\"" + this.replace("\"", "\"\"") + "\""
    }
    return this
}
