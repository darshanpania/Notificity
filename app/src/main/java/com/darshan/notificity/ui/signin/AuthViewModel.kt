package com.darshan.notificity.ui.signin

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.auth.models.AuthResult
import com.darshan.notificity.auth.models.AuthUiState
import com.darshan.notificity.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            if (authRepository.isSignedIn()) {
                val userData = authRepository.getCurrentUserData()
                _uiState.value = _uiState.value.copy(
                    currentUser = userData,
                    isAuthenticated = true
                )
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

    fun signOut(authType: AuthType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = authRepository.signOut()) {
                is AuthResult.Success -> {
                    _uiState.value = AuthUiState()
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Sign out failed"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun performAuthOperation(operation: suspend () -> AuthResult) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

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
        val userData = authRepository.getCurrentUserData()
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            currentUser = userData,
            isAuthenticated = true,
            error = null
        )
    }

    private fun handleAuthError(errorMessage: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = errorMessage
        )
    }
}