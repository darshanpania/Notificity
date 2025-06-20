package com.darshan.notificity.extensions

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.darshan.notificity.auth.AuthType

/**
 * Checks if the user is authenticated with Google provider.
 * @return true if user has Google as one of their authentication providers
 */
fun FirebaseUser.isGoogleAuthenticated(): Boolean {
    return !isAnonymous && providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }
}

/**
 * Checks if the user is authenticated with a specific AuthType.
 * @param authType The authentication type to check for
 * @return true if user is authenticated with the specified provider
 */
fun FirebaseUser.isAuthenticatedWith(authType: AuthType): Boolean {
    return when (authType) {
        AuthType.GOOGLE -> isGoogleAuthenticated()
        AuthType.ANONYMOUS -> isAnonymous
    }
}

/**
 * Gets the AuthType of the current user's primary authentication method.
 * @return AuthType representing the user's authentication method, or null if undetermined
 */
fun FirebaseUser.getAuthType(): AuthType? {
    return when {
        isAnonymous -> AuthType.ANONYMOUS
        isGoogleAuthenticated() -> AuthType.GOOGLE
        else -> null
    }
}