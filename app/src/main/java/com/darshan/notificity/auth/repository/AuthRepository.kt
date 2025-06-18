package com.darshan.notificity.auth.repository

import androidx.activity.ComponentActivity
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.manager.AuthManager
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.User
import com.darshan.notificity.auth.providers.base.AnonymousAuthProvider
import com.darshan.notificity.auth.providers.base.BaseAuthProvider
import com.darshan.notificity.auth.providers.base.GoogleOAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing Auth operations across different Auth providers.
 * Handles Google OAuth, Anonymous authentication, and user's session management.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authManager: AuthManager,
    private val providers: Map<AuthType, BaseAuthProvider>
) {

    /**
     * Authenticates user with Google OAuth.
     * @param activityContext The activity context required for Google sign-in flow
     * @return AuthResult containing success or error information
     */
    suspend fun signInWithGoogle(
        activityContext: ComponentActivity
    ): AuthResult {
        var googleAuthProvider = providers[AuthType.GOOGLE] as GoogleOAuthProvider

        return when (val result = googleAuthProvider.signInWithGoogle(activityContext)) {
            is AuthResult.Success -> authManager.handleAuthSuccess(googleAuthProvider)
            is AuthResult.Error -> result
        }
    }

    /**
     * Authenticates user anonymously without needing credentials.
     * @return AuthResult containing success or error information
     */
    suspend fun signInAnonymously(): AuthResult {
        var anonymousAuthProvider = providers[AuthType.ANONYMOUS] as AnonymousAuthProvider

        return when (val result = anonymousAuthProvider.signInAnonymously()) {
            is AuthResult.Success -> authManager.handleAuthSuccess(anonymousAuthProvider)
            is AuthResult.Error -> result
        }
    }

    /**
     * Signs out the current authenticated user.
     * @return AuthResult indicating success or failure of sign-out operation
     */
    suspend fun signOut(): AuthResult {
        val provider = authManager.getCurrentAuthProvider()
        return provider?.signOut() ?: AuthResult.Error(Exception("No authenticated user found"))
    }

    /**
     * Checks if a user is currently signed in.
     * @return true if user is authenticated, false otherwise
     */
    suspend fun isSignedIn(): Boolean {
        val provider = authManager.getCurrentAuthProvider() ?: return false
        return provider.isSignedIn()
    }

    /**
     * Retrieves current authenticated user's data.
     * @return User object if authenticated, null otherwise
     */
    suspend fun getCurrentUserData(): User? {
        return authManager.getCurrentUserData()
    }
}