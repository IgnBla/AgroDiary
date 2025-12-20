package com.agrodiary.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.UserEntity
import com.agrodiary.data.repository.AuthRepository
import com.agrodiary.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling authentication UI state and operations.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _registerUsername = MutableStateFlow("")
    val registerUsername: StateFlow<String> = _registerUsername.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword: StateFlow<String> = _registerConfirmPassword.asStateFlow()

    private val _registerDisplayName = MutableStateFlow("")
    val registerDisplayName: StateFlow<String> = _registerDisplayName.asStateFlow()

    private val _registerFarmName = MutableStateFlow("")
    val registerFarmName: StateFlow<String> = _registerFarmName.asStateFlow()

    private val _hasUsers = MutableStateFlow<Boolean?>(null)
    val hasUsers: StateFlow<Boolean?> = _hasUsers.asStateFlow()

    init {
        viewModelScope.launch {
            // Session initialization is handled by MainActivity
            // Only check for existing users here
            checkHasUsers()
        }
    }

    private suspend fun checkHasUsers() {
        _hasUsers.value = authRepository.hasUsers()
    }

    fun setLoginUsername(value: String) {
        _loginUsername.value = value
    }

    fun setLoginPassword(value: String) {
        _loginPassword.value = value
    }

    fun setRegisterUsername(value: String) {
        _registerUsername.value = value
    }

    fun setRegisterPassword(value: String) {
        _registerPassword.value = value
    }

    fun setRegisterConfirmPassword(value: String) {
        _registerConfirmPassword.value = value
    }

    fun setRegisterDisplayName(value: String) {
        _registerDisplayName.value = value
    }

    fun setRegisterFarmName(value: String) {
        _registerFarmName.value = value
    }

    fun login() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(
                username = _loginUsername.value,
                password = _loginPassword.value
            )
            _authState.value = when (result) {
                is AuthResult.Success -> {
                    clearLoginForm()
                    AuthState.Success
                }
                is AuthResult.Error -> AuthState.Error(result.message)
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(
                username = _registerUsername.value,
                password = _registerPassword.value,
                confirmPassword = _registerConfirmPassword.value,
                displayName = _registerDisplayName.value,
                farmName = _registerFarmName.value.takeIf { it.isNotBlank() }
            )
            _authState.value = when (result) {
                is AuthResult.Success -> {
                    clearRegisterForm()
                    _hasUsers.value = true
                    AuthState.Success
                }
                is AuthResult.Error -> AuthState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun updateProfile(user: UserEntity) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.updateProfile(user)
            _authState.value = when (result) {
                is AuthResult.Success -> AuthState.Success
                is AuthResult.Error -> AuthState.Error(result.message)
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun clearLoginForm() {
        _loginUsername.value = ""
        _loginPassword.value = ""
    }

    private fun clearRegisterForm() {
        _registerUsername.value = ""
        _registerPassword.value = ""
        _registerConfirmPassword.value = ""
        _registerDisplayName.value = ""
        _registerFarmName.value = ""
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
