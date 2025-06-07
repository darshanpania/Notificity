package com.darshan.notificity.enums

/**
 * Represents the user's notification permission status.
 *
 * @property code Integer code representing the status:
 * - 1: GRANTED — User has granted POST_NOTIFICATIONS permission.
 * - 2: DENIED — User has denied POST_NOTIFICATIONS permission.
 */
enum class NotificationPermissionStatus(val code: Int) {
    GRANTED(1),
    DENIED(2);

    override fun toString(): String = name.lowercase()
}
