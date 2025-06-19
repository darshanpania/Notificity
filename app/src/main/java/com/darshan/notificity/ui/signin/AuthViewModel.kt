package com.darshan.notificity.ui.signin

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.AuthUiState
import com.darshan.notificity.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling Auth operations - Google sign-in,
 * anonymous sign-in, and sign-out with UI state management.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkAuthState() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    authCheckCompleted = false
                )
            }

            try {
                val isLoggedIn = authRepository.isSignedIn()
                val userData = authRepository.getCurrentUserData()

                _uiState.update {
                    it.copy(
                        currentUser = userData,
                        isAuthenticated = isLoggedIn,
                        authCheckCompleted = true,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        authCheckCompleted = true,
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun signInWithGoogle(activityContext: ComponentActivity) {
        performAuthOperation {
            authRepository.signInWithGoogle(activityContext)
        }
    }

    fun signInAnonymously() {
        performAuthOperation {
            authRepository.signInAnonymously()
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = authRepository.signOut(context = context)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            currentUser = null,
                            authCheckCompleted = false,
                            isAuthenticated = false,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Sign out failed"
                        )
                    }
                }
            }
        }
    }

    private fun performAuthOperation(operation: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            when (val result = operation()) {
                is AuthResult.Success -> {
                    handleAuthSuccess()
                }
                is AuthResult.Error -> {
                    handleAuthError(result.exception.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private suspend fun handleAuthSuccess() {
        try {
            val userData = authRepository.getCurrentUserData()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentUser = userData,
                    isAuthenticated = true,
                    authCheckCompleted = true,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    authCheckCompleted = true,
                    error = "Failed to get user data: ${e.message}"
                )
            }
        }
    }

    private fun handleAuthError(errorMessage: String) {
        _uiState.update { it.copy(
            isLoading = false,
            error = errorMessage,
            authCheckCompleted = true
        ) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}