package com.darshan.notificity.auth.models

/**
 * Represents the result of an authentication operation.
 */
sealed class AuthResult {

    /**
     * Indicates a successful authentication.
     */
    object Success : AuthResult()

    /**
     * Indicates a failed authentication with the thrown [exception].
     */
    data class Error(val exception: Exception) : AuthResult()
}
