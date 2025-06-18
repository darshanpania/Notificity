package com.darshan.notificity.auth.providers.base

import androidx.activity.ComponentActivity
import com.darshan.notificity.auth.models.AuthResult

/**
 * Authentication provider interface for Google OAuth sign-in.
 * Extends [BaseAuthProvider] with methods specific to the Google signin.
 */
interface GoogleOAuthProvider : BaseAuthProvider {

    /**
     * Initiates Google OAuth sign-in flow.
     * @param activityContext The activity context required for Google sign-in UI
     * @return AuthResult containing success or error information
     */
    suspend fun signInWithGoogle(activityContext: ComponentActivity): AuthResult
}