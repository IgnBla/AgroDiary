package com.agrodiary.data.repository

import android.content.Context
import com.agrodiary.data.local.dao.UserDao
import com.agrodiary.data.local.entity.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling user authentication operations.
 * Provides login, registration, and session management functionality.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Note: Session initialization is handled by initializeSession() method
    // which should be called from MainActivity after composition

    suspend fun initializeSession() {
        val savedUserId = prefs.getLong(KEY_USER_ID, -1L)
        if (savedUserId != -1L) {
            val user = userDao.getUserById(savedUserId)
            if (user != null && user.isActive) {
                _currentUser.value = user
                _isLoggedIn.value = true
            } else {
                clearSession()
            }
        }
    }

    suspend fun login(username: String, password: String): AuthResult {
        if (username.isBlank()) {
            return AuthResult.Error("Введите имя пользователя")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Введите пароль")
        }

        val user = userDao.getUserByUsername(username.trim().lowercase())
            ?: return AuthResult.Error("Пользователь не найден")

        if (!user.isActive) {
            return AuthResult.Error("Аккаунт деактивирован")
        }

        val passwordHash = hashPassword(password)
        if (user.passwordHash != passwordHash) {
            return AuthResult.Error("Неверный пароль")
        }

        // Update last login time
        userDao.updateLastLogin(user.id)

        // Save session
        saveSession(user.id)

        _currentUser.value = user.copy(lastLoginAt = System.currentTimeMillis())
        _isLoggedIn.value = true

        return AuthResult.Success(user)
    }

    suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
        displayName: String,
        farmName: String? = null
    ): AuthResult {
        // Validation
        if (username.isBlank()) {
            return AuthResult.Error("Введите имя пользователя")
        }
        if (username.length < 3) {
            return AuthResult.Error("Имя пользователя должно содержать минимум 3 символа")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Введите пароль")
        }
        if (password.length < 4) {
            return AuthResult.Error("Пароль должен содержать минимум 4 символа")
        }
        if (password != confirmPassword) {
            return AuthResult.Error("Пароли не совпадают")
        }
        if (displayName.isBlank()) {
            return AuthResult.Error("Введите ваше имя")
        }

        val normalizedUsername = username.trim().lowercase()

        if (userDao.isUsernameExists(normalizedUsername)) {
            return AuthResult.Error("Пользователь с таким именем уже существует")
        }

        val passwordHash = hashPassword(password)
        val user = UserEntity(
            username = normalizedUsername,
            passwordHash = passwordHash,
            displayName = displayName.trim(),
            farmName = farmName?.trim()?.takeIf { it.isNotBlank() }
        )

        return try {
            val userId = userDao.insertUser(user)
            val createdUser = user.copy(id = userId)

            // Auto-login after registration
            saveSession(userId)
            _currentUser.value = createdUser
            _isLoggedIn.value = true

            AuthResult.Success(createdUser)
        } catch (e: Exception) {
            AuthResult.Error("Ошибка при регистрации: ${e.message}")
        }
    }

    suspend fun logout() {
        clearSession()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    suspend fun updateProfile(user: UserEntity): AuthResult {
        return try {
            userDao.updateUser(user.copy(updatedAt = System.currentTimeMillis()))
            _currentUser.value = user
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error("Ошибка при обновлении профиля: ${e.message}")
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String
    ): AuthResult {
        val user = _currentUser.value ?: return AuthResult.Error("Пользователь не авторизован")

        if (currentPassword.isBlank()) {
            return AuthResult.Error("Введите текущий пароль")
        }

        val currentHash = hashPassword(currentPassword)
        if (user.passwordHash != currentHash) {
            return AuthResult.Error("Неверный текущий пароль")
        }

        if (newPassword.isBlank()) {
            return AuthResult.Error("Введите новый пароль")
        }
        if (newPassword.length < 4) {
            return AuthResult.Error("Новый пароль должен содержать минимум 4 символа")
        }
        if (newPassword != confirmNewPassword) {
            return AuthResult.Error("Новые пароли не совпадают")
        }

        val newHash = hashPassword(newPassword)
        userDao.updatePassword(user.id, newHash)
        _currentUser.value = user.copy(passwordHash = newHash, updatedAt = System.currentTimeMillis())

        return AuthResult.Success(user)
    }

    suspend fun hasUsers(): Boolean {
        return userDao.getUserCount() > 0
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    private fun saveSession(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    private fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "agrodiary_auth_prefs"
        private const val KEY_USER_ID = "current_user_id"
    }
}

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
