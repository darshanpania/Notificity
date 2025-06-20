package com.darshan.notificity.auth

/**
 * Enum representing the different authentication providers supported by the application.
 */
enum class AuthType {
    /** Google OAuth provider */
    GOOGLE,

    /** Anonymous auth provider for guest users */
    ANONYMOUS
}