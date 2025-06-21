package com.darshan.notificity.analytics.domain

import com.darshan.notificity.enums.AppPermissions
import com.darshan.notificity.enums.NotificationPermissionStatus

/**
 * Interface for tracking permission-related analytics events.
 * Handles permission requests and permission status changes.
 */
interface PermissionAnalytics {
    /**
     * Tracks when a permission is requested from the user.
     *
     * @param permission The specific permission that was requested
     */
    fun onPermissionRequested(permission: AppPermissions)

    /**
     * Tracks when a permission status changes.
     *
     * @param permission The permission whose status changed
     * @param status The new status of the permission
     */

    fun onPermissionChanged(
        permission: AppPermissions,
        status: NotificationPermissionStatus
    )

    companion object {
        // Event name constants
        const val PERMISSION_REQUESTED = "permission_requested"
        const val PERMISSION_CHANGED = "permission_changed"

        // Parameter key constants
        const val PERMISSION_STATUS = "permission_status"
    }
}