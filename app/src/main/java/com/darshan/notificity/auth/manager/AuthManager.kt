package com.darshan.notificity.auth.manager

import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.User
import com.darshan.notificity.auth.providers.base.BaseAuthProvider
import com.darshan.notificity.auth.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jakarta.inject.Singleton
import javax.inject.Inject

/**
 * Manages auth-related operations like saving user data, fetching current user info and
 * identifying auth type/provider
 */
@Singleton
class AuthManager @Inject constructor(
    private val userRepository: UserRepository,
    private val authProviders: Map<AuthType, BaseAuthProvider>
) {

    /**
     * Handles post-login success: saves or updates user data.
     */
    suspend fun handleAuthSuccess(authProvider: BaseAuthProvider): AuthResult {
        val firebaseUser = authProvider.getCurrentUser() ?: return AuthResult.Error(
            Exception("Firebase user is null after successful authentication")
        )

        return saveOrUpdateUser(authProvider, firebaseUser)
    }

    /**
     * Saves new user or updates last login time for existing user.
     */
    private suspend fun saveOrUpdateUser(
        authProvider: BaseAuthProvider, firebaseUser: FirebaseUser
    ): AuthResult {
        return try {
            // Check if user already exists
            val existingUserResult = userRepository.getUser(firebaseUser.uid)

            if (existingUserResult.isSuccess) {
                val existingUser = existingUserResult.getOrNull()
                if (existingUser == null) {
                    // New user, save to FireStore
                    val newUser = authProvider.createUserFromFirebaseUser(firebaseUser)
                    userRepository.saveUser(newUser)
                } else {
                    // Existing user, update last login
                    userRepository.updateLastLogin(firebaseUser.uid)
                }
            } else {
                // Error getting user, try to save anyway
                val newUser = authProvider.createUserFromFirebaseUser(firebaseUser)
                userRepository.saveUser(newUser)
            }

            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Returns current user data from Firestore.
     */
    suspend fun getCurrentUserData(): User? {
        // Use any auth provider since Firebase auth state is global
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return null
        val result = userRepository.getUser(firebaseUser.uid)

        return if (result.isSuccess) {
            result.getOrNull()
        } else {
            null
        }
    }

    /**
     * Returns current auth provider, if available.
     */
    suspend fun getCurrentAuthProvider(): BaseAuthProvider? {
        val authType = getCurrentAuthType()
        return authType?.let { authProviders[it] }
    }

    /**
     * Returns current user's auth type.
     */
    suspend fun getCurrentAuthType(): AuthType? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return null
        val result = userRepository.getUser(firebaseUser.uid)

        return if (result.isSuccess) result.getOrNull()?.authType else null
    }
}