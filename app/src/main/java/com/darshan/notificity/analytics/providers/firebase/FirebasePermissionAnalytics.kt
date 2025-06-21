package com.darshan.notificity.analytics.providers.firebase

import com.darshan.notificity.analytics.domain.PermissionAnalytics
import com.darshan.notificity.analytics.mergeCommonParams
import com.darshan.notificity.auth.repository.AuthRepository
import com.darshan.notificity.enums.AppPermissions
import com.darshan.notificity.enums.NotificationPermissionStatus
import com.darshan.notificity.extensions.toBundle
import com.darshan.notificity.utils.Util.Companion.getEpoch
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase implementation of PermissionAnalytics interface.
 * Provides Firebase Analytics integration for permission-related events.
 *
 * @param firebaseAnalytics Firebase Analytics instance for logging events
 * @param authRepository Repository for accessing current user data
 */
class FirebasePermissionAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val authRepository: AuthRepository
) : PermissionAnalytics {

    private val analyticsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Logs an event with enhanced information.
     * Automatically adds current user ID, timestamp etc. to event parameters.
     */
    private suspend fun logEvent(
        eventName: String,
        parameters: Map<String, Any> = emptyMap()
    ) {
        val user = authRepository.getCurrentUserData()

        val enhancedParams = parameters.mergeCommonParams(user, getEpoch())
        firebaseAnalytics.logEvent(eventName, enhancedParams.toBundle())
    }

    override fun onPermissionRequested(permission: AppPermissions) {
        analyticsScope.launch {
            logEvent(
                eventName = "${permission.name}_${PermissionAnalytics.PERMISSION_REQUESTED}"
            )
        }
    }

    override fun onPermissionChanged(
        permission: AppPermissions,
        status: NotificationPermissionStatus
    ) {
        // Set as user property for segmentation
        firebaseAnalytics.setUserProperty("${permission.name}_permission", status.code.toString())

        analyticsScope.launch {
            logEvent(
                eventName = PermissionAnalytics.PERMISSION_CHANGED,
                parameters = mapOf(
                    PermissionAnalytics.PERMISSION_STATUS to status.code,
                )
            )
        }
    }
}