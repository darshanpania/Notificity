package com.darshan.notificity.auth.providers.base

import android.content.Context
import com.darshan.notificity.auth.models.User
import com.darshan.notificity.auth.models.AuthResult
import com.google.firebase.auth.FirebaseUser

/**
 * Base interface for all authentication providers defining common operations
 * for user authentication, session management, and user data retrieval.
 */
interface BaseAuthProvider {

    /**
     * Signs out the current authenticated user.
     * @param context the context
     * @return AuthResult indicating success or failure of the sign-out operation
     */
    suspend fun signOut(context: Context): AuthResult

    /**
     * Checks if a user is currently signed in with this provider.
     * @return true if user is authenticated, false otherwise
     */
    fun isSignedIn(): Boolean

    /**
     * Retrieves the current Firebase user instance.
     * @return FirebaseUser if authenticated, null otherwise
     */
    fun getCurrentUser(): FirebaseUser?

    /**
     * Converts a FirebaseUser to the [User] model.
     * @param firebaseUser The Firebase user to convert
     * @return User object containing relevant user information
     */
    suspend fun createUserFromFirebaseUser(firebaseUser: FirebaseUser): User
}

