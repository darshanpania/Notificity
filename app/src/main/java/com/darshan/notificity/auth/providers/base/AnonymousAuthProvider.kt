package com.darshan.notificity.auth.providers.base

import com.darshan.notificity.auth.models.AuthResult

/**
 * Authentication provider interface for anonymous user authentication.
 * Extends [BaseAuthProvider] with anonymous sign-in functionality.
 */
interface AnonymousAuthProvider : BaseAuthProvider {

    /**
     * Signs in the user anonymously without needing any credentials.
     * @return AuthResult containing success or error information
     */
    suspend fun signInAnonymously(): AuthResult
}