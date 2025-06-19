package com.darshan.notificity.auth.models

/**
 * UI state holder for authentication-related operations.
 *
 * This class encapsulates the current state of authentication, including whether an
 * operation is in progress, whether the user is authenticated, any errors encountered,
 * and the current authenticated user (if available).
 *
 * @property isGoogleLoading Indicates if a Google sign-in operation is currently in progress.
 * @property isAnonymousLoading Indicates if an anonymous sign-in operation is currently in progress.
 * @property currentUser The currently authenticated user, or null if no user is signed in.
 * @property error An error message from the last failed operation, or null if there was no error.
 * @property authCheckCompleted Whether the initial authentication check has been completed.
 *                              Useful to ensure that UI waits until the state is determined.
 * @property isAuthenticated True if the user is currently signed in, false otherwise.
 */
data class AuthUiState(
    val isGoogleLoading: Boolean = false,
    val isAnonymousLoading: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val authCheckCompleted: Boolean = false,
    val isAuthenticated: Boolean = false
)

