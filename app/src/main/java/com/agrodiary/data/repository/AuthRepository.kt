package com.agrodiary.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.agrodiary.data.local.dao.UserDao
import com.agrodiary.data.local.entity.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
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
    private val prefs by lazy { createSecurePrefs(context) }

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

        val verifiedUser = verifyPasswordAndUpgradeIfNeeded(user, password)
        if (verifiedUser == null) {
            return AuthResult.Error("Неверный пароль")
        }

        // Update last login time
        val loginTimestamp = System.currentTimeMillis()
        userDao.updateLastLogin(user.id, loginTimestamp)

        // Save session
        saveSession(user.id)

        val sessionUser = verifiedUser.copy(lastLoginAt = loginTimestamp)
        _currentUser.value = sessionUser
        _isLoggedIn.value = true

        return AuthResult.Success(sessionUser)
    }

    suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
        displayName: String,
        farmName: String? = null
    ): AuthResult {
        // Validation
        val normalizedUsername = username.trim().lowercase()
        val trimmedDisplayName = displayName.trim()
        if (normalizedUsername.isBlank()) {
            return AuthResult.Error("Введите имя пользователя")
        }
        if (normalizedUsername.length < 3) {
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
        if (trimmedDisplayName.isBlank()) {
            return AuthResult.Error("Введите ваше имя")
        }


        if (userDao.isUsernameExists(normalizedUsername)) {
            return AuthResult.Error("Пользователь с таким именем уже существует")
        }

        val passwordHash = hashPassword(password)
        val user = UserEntity(
            username = normalizedUsername,
            passwordHash = passwordHash.hash,
            passwordSalt = passwordHash.salt,
            displayName = trimmedDisplayName,
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
            val updatedAt = System.currentTimeMillis()
            val updatedUser = user.copy(updatedAt = updatedAt)
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser
            AuthResult.Success(updatedUser)
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

        if (!isPasswordValid(currentPassword, user)) {
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
        val updatedAt = System.currentTimeMillis()
        userDao.updatePassword(user.id, newHash.hash, newHash.salt, updatedAt)
        val updatedUser = user.copy(
            passwordHash = newHash.hash,
            passwordSalt = newHash.salt,
            updatedAt = updatedAt
        )
        _currentUser.value = updatedUser

        return AuthResult.Success(updatedUser)
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

    private data class PasswordHash(
        val hash: String,
        val salt: String
    )

    private val secureRandom = SecureRandom()

    private fun createSecurePrefs(context: Context): android.content.SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private suspend fun verifyPasswordAndUpgradeIfNeeded(
        user: UserEntity,
        password: String
    ): UserEntity? {
        val salt = user.passwordSalt
        if (salt.isNullOrBlank()) {
            val legacyHash = legacyHashPassword(password)
            if (legacyHash != user.passwordHash) {
                return null
            }
            val upgradedHash = hashPassword(password)
            val updatedAt = System.currentTimeMillis()
            userDao.updatePassword(user.id, upgradedHash.hash, upgradedHash.salt, updatedAt)
            return user.copy(
                passwordHash = upgradedHash.hash,
                passwordSalt = upgradedHash.salt,
                updatedAt = updatedAt
            )
        }

        val saltBytes = Base64.getDecoder().decode(salt)
        val computedHash = hashPasswordWithSalt(password, saltBytes)
        return if (computedHash == user.passwordHash) user else null
    }

    private fun isPasswordValid(password: String, user: UserEntity): Boolean {
        val salt = user.passwordSalt
        return if (salt.isNullOrBlank()) {
            legacyHashPassword(password) == user.passwordHash
        } else {
            val saltBytes = Base64.getDecoder().decode(salt)
            hashPasswordWithSalt(password, saltBytes) == user.passwordHash
        }
    }

    private fun hashPassword(password: String): PasswordHash {
        val saltBytes = ByteArray(SALT_LENGTH_BYTES).apply { secureRandom.nextBytes(this) }
        val hash = hashPasswordWithSalt(password, saltBytes)
        return PasswordHash(
            hash = hash,
            salt = Base64.getEncoder().encodeToString(saltBytes)
        )
    }

    private fun hashPasswordWithSalt(password: String, saltBytes: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), saltBytes, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val hashBytes = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    private fun legacyHashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "agrodiary_auth_prefs"
        private const val KEY_USER_ID = "current_user_id"
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val PBKDF2_ITERATIONS = 65536
        private const val KEY_LENGTH_BITS = 256
        private const val SALT_LENGTH_BYTES = 16
    }
}

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
