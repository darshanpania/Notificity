package com.darshan.notificity.analytics.providers.firebase

import com.darshan.notificity.analytics.constants.AnalyticsConstants
import com.darshan.notificity.analytics.domain.AppAnalytics
import com.darshan.notificity.analytics.enums.Screen
import com.darshan.notificity.analytics.mergeCommonParams
import com.darshan.notificity.auth.repository.AuthRepository
import com.darshan.notificity.extensions.toBundle
import com.darshan.notificity.utils.Util.Companion.getEpoch
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Firebase implementation of [AppAnalytics] interface.
 * Provides Firebase Analytics integration for general app events.
 *
 * @param firebaseAnalytics Firebase Analytics instance for logging events
 * @param authRepository Repository for accessing current user data
 */
class FirebaseAppAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val authRepository: AuthRepository
) : AppAnalytics {
    
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
    
    override fun onAppLaunch(source: String) {
        analyticsScope.launch {
            logEvent(FirebaseAnalytics.Event.APP_OPEN, mapOf(
                AnalyticsConstants.SOURCE to source
            ))
        }
    }

    override fun onScreenViewed(screen: Screen) {
        analyticsScope.launch {
            logEvent(
                eventName = FirebaseAnalytics.Event.SCREEN_VIEW,
                parameters = mapOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to screen.screenName,
                    FirebaseAnalytics.Param.SCREEN_CLASS to screen.screenClass
                )
            )
        }
    }

    override fun onThemeToggleClicked(theme: String) {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.THEME_TOGGLE,
                parameters = mapOf(
                    AppAnalytics.THEME to theme
                )
            )
        }
    }

    override fun onNotificationListOpened(appName: String, total: Int) {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.NOTIFICATION_LIST_VIEWED,
                parameters = mapOf(
                    AppAnalytics.APP_NAME to appName,
                    AppAnalytics.TOTAL_NOTIFICATIONS to total
                )
            )
        }
    }

    override fun onRecommendAppClicked() {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.RECOMMEND_APP_CLICKED
            )
        }
    }

    override fun onPrivacyPolicyClicked() {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.PRIVACY_POLICY_CLICKED
            )
        }
    }

    override fun onContributorProfileClicked(name: String) {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.CONTRIBUTOR_CLICKED,
                parameters = mapOf(
                    AppAnalytics.CONTRIBUTOR_NAME to name
                )
            )
        }
    }

    override fun onBuyMeCoffeeClicked() {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.BUY_ME_COFFEE_CLICKED
            )
        }
    }

    override fun onErrorOccurred(screen: Screen, error: String) {
        analyticsScope.launch {
            logEvent(
                eventName = AppAnalytics.ERROR_OCCURRED,
                parameters = mapOf(
                    FirebaseAnalytics.Param.SCREEN_NAME to screen.screenName,
                    FirebaseAnalytics.Param.SCREEN_CLASS to screen.screenClass,
                    AppAnalytics.ERROR to error,
                )
            )
        }
    }
}