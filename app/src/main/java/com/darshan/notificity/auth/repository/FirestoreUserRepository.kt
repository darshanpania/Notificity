package com.darshan.notificity.auth.repository

import com.darshan.notificity.auth.models.User
import com.darshan.notificity.constants.FirestoreConstants
import com.darshan.notificity.constants.FirestoreConstants.FIELD_LAST_LOGIN
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation of [UserRepository].
 *
 * Stores and retrieves user data in the Firestore "users" collection.
 *
 * @param firestore An instance of [FirebaseFirestore] used for interacting with Firestore.
 */
@Singleton
class FirestoreUserRepository @Inject constructor(
    firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection(FirestoreConstants.USERS_COLLECTION)

    /**
     * Saves the user object in the Firestore "users" collection.
     *
     * If a document with the same user ID exists, it will be overwritten.
     */
    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves a user document by ID from the "users" collection.
     */
    override suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the [FIELD_LAST_LOGIN] field of the user document with the current system time in milliseconds.
     */
    override suspend fun updateLastLogin(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update(FIELD_LAST_LOGIN, FieldValue.serverTimestamp())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}