package com.darshan.notificity.auth.providers.firebase

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.darshan.notificity.R
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.User
import com.darshan.notificity.auth.providers.base.GoogleOAuthProvider
import com.darshan.notificity.extensions.isGoogleAuthenticated
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of Google OAuth authentication provider.
 * Handles Google sign-in using Credential Manager API and Firebase Authentication.
 */
@Singleton
class FirebaseGoogleAuthProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GoogleOAuthProvider {

    private val TAG = "FirebaseGoogleAuthProvider"

    /**
     * Initiates Google OAuth sign-in flow using Credential Manager.
     * @param activityContext The activity context required for launching credential manager UI
     * @return AuthResult containing success or error information
     */
    override suspend fun signInWithGoogle(activityContext: ComponentActivity): AuthResult {
        return try {
            val result = launchCredentialManager(activityContext)
            handleSignIn(result.credential)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Launches the Credential Manager UI to retrieve Google ID token.
     * @param context The activity context for launching the credential manager
     * @return GetCredentialResponse containing the selected credential
     * @throws Exception if credential retrieval fails
     */
    private suspend fun launchCredentialManager(
        context: ComponentActivity
    ): GetCredentialResponse {
        try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(context.getString(R.string.web_client_id)) // Server's client ID
                .setFilterByAuthorizedAccounts(false) // Only show accounts previously used to sign in.
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            // Launch Credential Manager UI
            return credentialManager.getCredential(
                request = request,
                context = context
            )
        } catch (e: GetCredentialException) {
            throw Exception("Google Sign-In failed: ${e.message}")
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Processes the credential returned from Credential Manager.
     * @param credential The credential object to validate and process
     * @return AuthResult indicating success or failure of credential processing
     */
    private suspend fun handleSignIn(credential: Credential): AuthResult {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            // Sign in to Firebase with using the token
            return firebaseAuthWithGoogle(googleIdToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
            return AuthResult.Error(
                Exception(
                    "Unexpected credential type"
                )
            )
        }
    }

    /**
     * Authenticates with Firebase using the Google ID token.
     * @param googleIdToken The Google ID token to authenticate with Firebase
     * @return AuthResult indicating success or failure of Firebase authentication
     */
    private suspend fun firebaseAuthWithGoogle(googleIdToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = firebaseAuth.signInWithCredential(credential).await().user
            user?.let { user ->
                AuthResult.Success
            } ?: AuthResult.Error(Exception("Firebase authentication failed"))
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            AuthResult.Error(
                Exception(
                    "Firebase authentication failed: " + e.message
                )
            )
        }
    }

    /**
     * Signs out the current Google-authenticated user from Firebase and clears credential state.
     * @return AuthResult indicating success or failure of sign-out operation
     */
    override suspend fun signOut(context: Context): AuthResult = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signOut()
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // no-op
            }
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    /**
     * Checks if a Google-authenticated user is currently signed in.
     * @return true if user is signed in with Google provider, false otherwise
     */
    override fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser?.isGoogleAuthenticated() == true
    }

    /**
     * Retrieves the current Google-authenticated Firebase user.
     * @return FirebaseUser if signed in with Google provider, null otherwise
     */
    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser?.takeIf { it.isGoogleAuthenticated() }
    }

    /**
     * Converts a FirebaseUser to the application's User model for Google authentication.
     * @param firebaseUser The Firebase user to convert
     * @return User object containing Google user information and profile data
     */
    override suspend fun createUserFromFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email,
            name = firebaseUser.displayName,
            profilePicUrl = firebaseUser.photoUrl?.toString(),
            authType = AuthType.GOOGLE
        )
    }
}