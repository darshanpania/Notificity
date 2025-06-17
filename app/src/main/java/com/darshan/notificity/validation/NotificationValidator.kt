package com.darshan.notificity.validation

import android.app.Notification

/**
 * Utility to validate posted notifications and filter out summary or irrelevant notifications.
 *
 * Provides helper utilities to identify group summary notifications that should be skipped when
 * processing notifications for tracking or display.
 */
object NotificationValidator {

    private val summaryPattern1 =
        Regex("\\d+\\s+(new\\s+)?(message|msg)s?", RegexOption.IGNORE_CASE)
    private val summaryPattern2 = Regex("You have \\d+ (new|unread)", RegexOption.IGNORE_CASE)

    /** Returns true if the notification is a group summary. */
    fun isGroupSummary(notification: Notification): Boolean {
        return (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
    }

    /**
     * Returns true if text matches common summary/count notification patterns. These typically
     * include messages like "3 new messages" or "You have 5 unread"
     */
    fun isSummaryText(text: String): Boolean {
        return summaryPattern1.matches(text) || summaryPattern2.matches(text)
    }

    /** Returns true if both title and content are non-empty. */
    fun isValidContent(title: String, content: String): Boolean {
        return title.isNotEmpty() && content.isNotEmpty()
    }
}
