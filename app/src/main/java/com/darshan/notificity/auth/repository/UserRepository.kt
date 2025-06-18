package com.darshan.notificity.auth.repository

import com.darshan.notificity.auth.models.User

/**
 * Repository interface for managing user-related data.
 *
 * This abstraction allows different implementations (e.g., Firebase, local DB)
 * to handle storing, retrieving, and updating user information.
 */
interface UserRepository {

    /**
     * Saves a new user or updates an existing user in the data source.
     *
     * @param user The [User] object to be saved.
     * @return [Result.success] if operation succeeds, or [Result.failure] with an exception.
     */
    suspend fun saveUser(user: User): Result<Unit>

    /**
     * Retrieves user details by user ID.
     *
     * @param userId The unique identifier of the user.
     * @return [Result.success] with the user object if found (or null if not),
     *         or [Result.failure] if the operation fails.
     */
    suspend fun getUser(userId: String): Result<User?>

    /**
     * Updates the last login timestamp of the user.
     *
     * @param userId The unique identifier of the user.
     * @return [Result.success] if update succeeds, or [Result.failure] with an exception.
     */
    suspend fun updateLastLogin(userId: String): Result<Unit>
}
