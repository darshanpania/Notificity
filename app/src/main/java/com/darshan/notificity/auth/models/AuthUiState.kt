package com.darshan.notificity.auth.models

/**
 * Holds the UI state related to authentication.
 *
 * @property isLoading Whether an authentication operation is in progress.
 * @property currentUser The currently authenticated user, if any.
 * @property error Error message from the last failed operation, if any.
 * @property isAuthenticated Indicates if the user is currently authenticated.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val isAuthChecked: Boolean = false,
    val isAuthenticated: Boolean = false
)

