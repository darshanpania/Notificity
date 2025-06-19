package com.darshan.notificity.auth.models

/**
 * UI state holder for authentication-related operations.
 *
 * This class encapsulates the current state of authentication, including whether an
 * operation is in progress, whether the user is authenticated, any errors encountered,
 * and the current authenticated user (if available).
 *
 * @property isLoading Indicates if an authentication operation (sign-in, sign-out, etc.) is in progress.
 * @property currentUser The currently authenticated user, or null if no user is signed in.
 * @property error An error message from the last failed operation, or null if there was no error.
 * @property authCheckCompleted Whether the initial authentication check has been completed.
 *                              Useful to ensure that UI waits until the state is determined.
 * @property isAuthenticated True if the user is currently signed in, false otherwise.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val authCheckCompleted: Boolean = false,
    val isAuthenticated: Boolean = false
)

