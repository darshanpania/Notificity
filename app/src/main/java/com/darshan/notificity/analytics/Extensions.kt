package com.darshan.notificity.analytics

import com.darshan.notificity.analytics.constants.AnalyticsConstants
import com.darshan.notificity.auth.models.User

/**
 * Extension function for Map<String, Any> to merge common analytics parameters.
 *
 * @param user Optional user object to extract user ID from. If null, no user ID is added.
 * @param timestamp Timestamp parameter (currently unused, timestamp is generated via getEpoch())
 * @return A new map containing the original parameters plus common analytics parameters
 */
fun Map<String, Any>.mergeCommonParams(user: User?, timestamp: Long): Map<String, Any> {
    val enhancedParams = toMutableMap()
    enhancedParams[AnalyticsConstants.TIMESTAMP] = timestamp
    user?.id?.let {
        enhancedParams[AnalyticsConstants.PARAM_USER_ID] = it
    }
    return enhancedParams
}