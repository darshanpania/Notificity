package com.darshan.notificity.analytics.providers.firebase

import com.darshan.notificity.analytics.domain.AuthAnalytics
import com.darshan.notificity.analytics.mergeCommonParams
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.User
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
 * Firebase implementation of [AuthAnalytics] interface.
 * Provides Firebase Analytics integration for authentication events.
 *
 * @param firebaseAnalytics Firebase Analytics instance for logging events
 * @param authRepository Repository for accessing current user data
 */
class FirebaseAuthAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val authRepository: AuthRepository
) : AuthAnalytics {

    private val analyticsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Logs an event with enhanced information.
     * Automatically adds current user ID, timestamp etc. to event parameters.
     */
    private suspend fun logEvent(
        user: User? = null,
        eventName: String,
        parameters: Map<String, Any> = emptyMap()
    ) {
        val user = user ?: authRepository.getCurrentUserData()

        val enhancedParams = parameters.mergeCommonParams(user, getEpoch())
        firebaseAnalytics.logEvent(eventName, enhancedParams.toBundle())
    }

    override fun onSigninAttempt(provider: AuthType) {
        analyticsScope.launch {
            logEvent(
                eventName = AuthAnalytics.Companion.EVENT_SIGNIN_ATTEMPT,
                parameters = mapOf(
                    AuthAnalytics.Companion.PARAM_PROVIDER to provider.name,
                )
            )
        }
    }

    override fun onSigninSuccess(user: User) {
        firebaseAnalytics.setUserId(user.id)
        firebaseAnalytics.setUserProperty(AuthAnalytics.PARAM_PROVIDER, user.authType.name)
        firebaseAnalytics.setUserProperty(AuthAnalytics.PARAM_EMAIL, user.email)
        firebaseAnalytics.setUserProperty(AuthAnalytics.PARAM_NAME, user.name)

        analyticsScope.launch {
            logEvent(
                user = user,
                eventName = AuthAnalytics.EVENT_SIGNIN_SUCCESS,
                parameters = mapOf(
                    AuthAnalytics.PARAM_PROVIDER to user.authType.name,
                )
            )
        }
    }

    override fun onSigninFailure(provider: AuthType, error: String) {
        analyticsScope.launch {
            logEvent(
                eventName = AuthAnalytics.EVENT_SIGNIN_FAILURE,
                parameters = mapOf(
                    AuthAnalytics.PARAM_PROVIDER to provider.name,
                    AuthAnalytics.PARAM_ERROR to error
                )
            )
        }
    }

    override fun onSignout() {
        analyticsScope.launch {
            logEvent(
                eventName = AuthAnalytics.EVENT_SIGNOUT
            )
        }
    }
}