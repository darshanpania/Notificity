package com.darshan.notificity.analytics.core

import com.darshan.notificity.analytics.domain.AppAnalytics
import com.darshan.notificity.analytics.domain.AuthAnalytics
import com.darshan.notificity.analytics.domain.PermissionAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager providing single point of access to all analytics services.
 * Uses dependency injection to provide analytics functionality for different app domains.
 *
 * @param auth Authentication-related analytics service
 * @param app General app analytics service
 * @param permission Permission-related analytics service
 */
@Singleton
class AnalyticsManager @Inject constructor(
    val auth: AuthAnalytics,
    val app: AppAnalytics,
    val permission: PermissionAnalytics,
)