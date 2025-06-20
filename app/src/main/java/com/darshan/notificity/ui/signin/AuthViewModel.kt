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
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        authCheckCompleted = true,
                        error = e.message
                    )
                }
            }
        }
    }

    fun signInWithGoogle(activityContext: ComponentActivity) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isGoogleLoading = true, error = null)
            }

            when (val result = authRepository.signInWithGoogle(activityContext)) {
                is AuthResult.Success -> {
                    handleAuthSuccess()
                }
                is AuthResult.Error -> {
                    handleAuthError(result.exception.message ?: "Google sign-in failed")
                }
            }

            _uiState.update {
                it.copy(isGoogleLoading = false)
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isAnonymousLoading = true, error = null)
            }

            when (val result = authRepository.signInAnonymously()) {
                is AuthResult.Success -> {
                    handleAuthSuccess()
                }
                is AuthResult.Error -> {
                    handleAuthError(result.exception.message ?: "Anonymous sign-in failed")
                }
            }

            _uiState.update {
                it.copy(isAnonymousLoading = false)
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            when (val result = authRepository.signOut(context = context)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            currentUser = null,
                            authCheckCompleted = false,
                            isAuthenticated = false,
                            error = null
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.exception.message ?: "Sign out failed"
                        )
                    }
                }
            }
        }
    }

    private suspend fun handleAuthSuccess() {
        try {
            val userData = authRepository.getCurrentUserData()
            _uiState.update {
                it.copy(
                    currentUser = userData,
                    isAuthenticated = true,
                    authCheckCompleted = true,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    authCheckCompleted = true,
                    error = "Failed to get user data: ${e.message}"
                )
            }
        }
    }

    private fun handleAuthError(errorMessage: String) {
        _uiState.update { it.copy(
            error = errorMessage,
            authCheckCompleted = true
        ) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}