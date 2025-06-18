package com.darshan.notificity.auth.providers.firebase

import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.User
import com.darshan.notificity.auth.providers.base.AnonymousAuthProvider
import com.darshan.notificity.extensions.isAuthenticatedWith
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of anonymous authentication provider.
 * Handles anonymous user sign-in and session management without requiring credentials.
 */
@Singleton
open class FirebaseAnonymousAuthProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AnonymousAuthProvider {

    /**
     * Signs in the user anonymously without requiring any credentials.
     * Creates a temporary Firebase user session that persists until sign-out.
     * @return AuthResult indicating success or failure of anonymous sign-in
     */
    override suspend fun signInAnonymously(): AuthResult {
        return try {
            firebaseAuth.signInAnonymously().await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Signs out the current anonymous user from Firebase.
     * @return AuthResult indicating success or failure of sign-out operation
     */
    override suspend fun signOut(): AuthResult {
        return try {
            firebaseAuth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Checks if an anonymous user is currently signed in.
     * @return true if user is authenticated anonymously, false otherwise
     */
    override fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser?.isAuthenticatedWith(AuthType.ANONYMOUS) == true
    }

    /**
     * Retrieves the current anonymous Firebase user.
     * @return FirebaseUser if signed in anonymously, null otherwise
     */
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser?.takeIf { it.isAuthenticatedWith(AuthType.ANONYMOUS) }
    }

    /**
     * Converts a FirebaseUser to the application's User model for anonymous authentication.
     * @param firebaseUser The Firebase user to convert
     * @return User object containing anonymous user information with minimal data
     */
    override suspend fun createUserFromFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            authType = AuthType.ANONYMOUS
        )
    }
}