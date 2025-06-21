package com.darshan.notificity.analytics.domain

import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.User

/**
 * Interface for tracking authentication-related analytics events.
 * Handles sign-in attempts, successes, failures, and sign-out events.
 */
interface AuthAnalytics {
    /**
     * Tracks when a user attempts to sign in.
     *
     * @param authProviderType The type of authentication provider used
     */
    fun onSigninAttempt(authProviderType: AuthType)

    /**
     * Tracks successful user sign-in events.
     *
     * @param user The user object containing user details after successful sign-in
     */
    fun onSigninSuccess(user: User)

    /**
     * Tracks failed sign-in attempts.
     *
     * @param provider The authentication provider that failed
     * @param error Description of the sign-in error
     */
    fun onSigninFailure(provider: AuthType, error: String)

    /**
     * Tracks when a user signs out of the application.
     */
    fun onSignout()

    companion object {
        // Event name constants
        const val EVENT_SIGNIN_ATTEMPT = "signin_attempt"
        const val EVENT_SIGNIN_SUCCESS = "signin_success"
        const val EVENT_SIGNIN_FAILURE = "signin_failure"
        const val EVENT_SIGNOUT = "signout"

        // Parameter key constants
        const val PARAM_PROVIDER = "provider"
        const val PARAM_EMAIL = "email"
        const val PARAM_NAME = "name"
        const val PARAM_ERROR = "error"
    }
}