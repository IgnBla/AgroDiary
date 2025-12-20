# Auth Module - Bug Report & Refactoring Plan

**Module:** Authentication & Registration
**Version:** 1.4.0
**Date:** 2025-12-20
**Author:** Senior QA Engineer + Senior Android Developer

---

## PART 1: QA TESTING REPORT (Senior QA Engineer)

### 1.1 Test Plan - Regression Testing

#### Test Scope
| Category | Test Cases | Priority |
|----------|-----------|----------|
| Login Flow | 8 | Critical |
| Registration Flow | 10 | Critical |
| Session Management | 6 | High |
| Password Security | 5 | High |
| Input Validation | 12 | Medium |
| UI/UX | 8 | Medium |
| Edge Cases | 6 | Low |

#### Test Environment
- Platform: Android 8.0+ (API 26+)
- Build Type: Debug
- Database: Room SQLite (Version 2)

---

### 1.2 Bug Report

#### CRITICAL BUGS

##### BUG-001: Password Hashing Without Salt
- **Severity:** Critical
- **File:** `AuthRepository.kt:204-206`
- **Description:** Password hashing uses SHA-256 without salt, making it vulnerable to rainbow table attacks
- **Code:**
```kotlin
private fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
```
- **Impact:** User passwords can be compromised if database is leaked
- **Recommendation:** Use bcrypt, PBKDF2, or Argon2 with unique salt per user

---

##### BUG-002: Race Condition in Auth Initialization
- **Severity:** Critical
- **Files:**
  - `AuthRepository.kt:32-38` (init block)
  - `MainActivity.kt:77-84` (LaunchedEffect)
  - `AuthViewModel.kt:54` (init block)
- **Description:** `isLoggedIn` is set to `true` in AuthRepository's init block before user existence is verified. Then `initializeSession()` is called both in MainActivity and AuthViewModel, causing potential race conditions.
- **Code (AuthRepository.kt:32-38):**
```kotlin
init {
    // Check for saved session on initialization
    val savedUserId = prefs.getLong(KEY_USER_ID, -1L)
    if (savedUserId != -1L) {
        _isLoggedIn.value = true  // BUG: Set before verification
    }
}
```
- **Impact:** App may briefly show incorrect auth state; potential navigation bugs
- **Recommendation:** Remove init block login check; rely solely on `initializeSession()`

---

#### HIGH SEVERITY BUGS

##### BUG-003: Missing Logout Functionality in Settings
- **Severity:** High
- **File:** Settings module (missing integration)
- **Description:** No logout button exists in Settings screen. Users cannot log out of their accounts.
- **Impact:** Users are locked into their session until app data is cleared
- **Recommendation:** Add logout button in SettingsScreen that calls `authViewModel.logout()` and navigates to LoginScreen

---

##### BUG-004: Unused Variable Warning - isLoggedIn
- **Severity:** High (Code Quality)
- **File:** `MainActivity.kt:64`
- **Description:** Variable `isLoggedIn` is collected but never used
- **Code:**
```kotlin
val isLoggedIn by authRepository.isLoggedIn.collectAsState()  // Never used
```
- **Compiler Warning:** `Variable 'isLoggedIn' is never used`
- **Impact:** Unnecessary memory allocation; confusing code
- **Recommendation:** Remove unused variable or implement reactive navigation based on it

---

##### BUG-005: Unused Variable in RegisterScreenContent
- **Severity:** Medium
- **File:** `RegisterScreen.kt:352`
- **Description:** `passwordVisible` variable is declared but not properly used in Preview content
- **Code:**
```kotlin
var passwordVisible by remember { mutableStateOf(false) }  // Not used for visibility toggle
```
- **Compiler Warning:** `Variable 'passwordVisible' is never used`
- **Impact:** Code inconsistency; preview doesn't match actual behavior
- **Recommendation:** Either use the variable or remove it

---

##### BUG-006: Unencrypted SharedPreferences
- **Severity:** High (Security)
- **File:** `AuthRepository.kt:24, 196-201`
- **Description:** User session ID is stored in plain SharedPreferences
- **Code:**
```kotlin
private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
// ...
private fun saveSession(userId: Long) {
    prefs.edit().putLong(KEY_USER_ID, userId).apply()
}
```
- **Impact:** Session can be hijacked on rooted devices
- **Recommendation:** Use `EncryptedSharedPreferences` from AndroidX Security library

---

#### MEDIUM SEVERITY BUGS

##### BUG-007: No Username Character Validation
- **Severity:** Medium
- **File:** `AuthRepository.kt:93-97, 112`
- **Description:** Username only checks for blank and minimum length, allows special characters and spaces
- **Code:**
```kotlin
if (username.isBlank()) { ... }
if (username.length < 3) { ... }
val normalizedUsername = username.trim().lowercase()  // Only trims, doesn't validate chars
```
- **Impact:** Usernames like "   " (spaces), "user@#$" are technically valid
- **Recommendation:** Add regex validation: `^[a-z0-9_]+$`

---

##### BUG-008: No Brute Force Protection
- **Severity:** Medium (Security)
- **File:** `AuthRepository.kt:53-83`
- **Description:** No limit on login attempts; no delay or lockout mechanism
- **Impact:** Vulnerable to brute-force password attacks
- **Recommendation:** Implement attempt counter with exponential backoff or account lockout

---

##### BUG-009: Weak Password Policy
- **Severity:** Medium
- **File:** `AuthRepository.kt:101-103`
- **Description:** Password only requires minimum 4 characters, no complexity requirements
- **Code:**
```kotlin
if (password.length < 4) {
    return AuthResult.Error("Пароль должен содержать минимум 4 символа")
}
```
- **Impact:** Users can set weak passwords like "1234"
- **Recommendation:** Require at least 6 characters with letters and numbers

---

##### BUG-010: hasUsers State Not Used in UI Flow
- **Severity:** Low
- **File:** `AuthViewModel.kt:50-62`
- **Description:** `hasUsers` is checked but the result doesn't affect navigation logic
- **Code:**
```kotlin
private val _hasUsers = MutableStateFlow<Boolean?>(null)
val hasUsers: StateFlow<Boolean?> = _hasUsers.asStateFlow()
// ... Never used in screens
```
- **Impact:** First-time users still see login screen instead of going directly to registration
- **Recommendation:** If no users exist, auto-navigate to RegisterScreen

---

#### LOW SEVERITY BUGS

##### BUG-011: Unused Imports
- **Severity:** Low
- **Files:**
  - `LoginScreen.kt:14` - `KeyboardOptions`
  - `RegisterScreen.kt:14` - `KeyboardOptions`
- **Description:** Imports declared but not used
- **Recommendation:** Remove unused imports

---

##### BUG-012: Code Duplication in Preview Functions
- **Severity:** Low (Maintainability)
- **Files:** `LoginScreen.kt:256-361`, `RegisterScreen.kt:336-458`
- **Description:** Preview content functions duplicate main screen UI code
- **Impact:** Changes to main UI may not be reflected in previews
- **Recommendation:** Extract shared composables or remove duplicate preview content

---

### 1.3 Test Execution Results

| Test Case | Status | Notes |
|-----------|--------|-------|
| Login with valid credentials | PASS | Works correctly |
| Login with invalid password | PASS | Shows error message |
| Login with non-existent user | PASS | Shows "Пользователь не найден" |
| Login with empty fields | PASS | Shows validation errors |
| Registration with valid data | PASS | Creates user and logs in |
| Registration with existing username | PASS | Shows duplicate error |
| Registration password mismatch | PASS | Shows "Пароли не совпадают" |
| Session persistence | PASS | User stays logged in after app restart |
| Logout functionality | FAIL | BUG-003: No logout button |
| Password visibility toggle | PASS | Works on both screens |
| Keyboard navigation | PASS | Focus moves correctly |

---

## PART 2: REFACTORING PLAN (Senior Android Developer)

### 2.1 Additional Issues Found

##### DEV-001: AuthResult in Wrong File
- **File:** `AuthRepository.kt:215-218`
- **Description:** `AuthResult` sealed class is defined inside repository file
- **Recommendation:** Move to separate file `AuthResult.kt` in domain/model package

---

##### DEV-002: Double Navigation on Success
- **Files:** `LoginScreen.kt:78-81`, `RegisterScreen.kt:82-85`
- **Description:** Both `onLoginSuccess()/onRegisterSuccess()` and `viewModel.resetState()` are called
- **Potential Issue:** State reset might cause issues if navigation is slow

---

##### DEV-003: Missing Database Migration
- **File:** `AppDatabase.kt:38`
- **Description:** Version bumped to 2 with `fallbackToDestructiveMigration()` in DatabaseModule
- **Impact:** All existing data will be lost on upgrade
- **Recommendation:** Add proper migration from version 1 to 2

---

### 2.2 Refactoring Priority List

| Priority | Task | Effort | Risk |
|----------|------|--------|------|
| P0 | Fix BUG-002: Remove race condition | Low | Low |
| P0 | Fix BUG-001: Implement proper password hashing | Medium | Medium |
| P1 | Fix BUG-003: Add logout to Settings | Low | Low |
| P1 | Fix BUG-006: Use EncryptedSharedPreferences | Low | Low |
| P1 | Add database migration | Medium | High |
| P2 | Fix BUG-007: Username validation | Low | Low |
| P2 | Fix BUG-008: Add brute force protection | Medium | Low |
| P2 | Fix BUG-009: Stronger password policy | Low | Low |
| P3 | Fix BUG-004, BUG-005: Remove unused variables | Low | None |
| P3 | Fix BUG-011: Remove unused imports | Low | None |
| P3 | Refactor: Extract AuthResult to separate file | Low | None |

---

### 2.3 Code to Remove (Unused Code)

1. **MainActivity.kt:64** - Remove unused `isLoggedIn` variable
2. **RegisterScreen.kt:352** - Fix or remove `passwordVisible` in preview
3. **LoginScreen.kt:14** - Remove unused `KeyboardOptions` import
4. **RegisterScreen.kt:14** - Remove unused `KeyboardOptions` import
5. **AuthViewModel.kt:50-62** - Either use `hasUsers` or remove it

---

### 2.4 Recommended Architecture Improvements

1. **Create UseCase layer for Auth:**
   - `LoginUseCase`
   - `RegisterUseCase`
   - `LogoutUseCase`
   - `ValidateCredentialsUseCase`

2. **Move AuthResult to domain layer:**
   ```
   domain/
   └── model/
       └── AuthResult.kt
   ```

3. **Create PasswordValidator utility:**
   ```kotlin
   object PasswordValidator {
       fun validate(password: String): ValidationResult
       fun hash(password: String, salt: ByteArray): String
       fun verify(password: String, hash: String, salt: ByteArray): Boolean
   }
   ```

4. **Add SessionManager for cleaner session handling:**
   ```kotlin
   class SessionManager @Inject constructor(
       private val encryptedPrefs: SharedPreferences
   ) {
       fun saveSession(userId: Long)
       fun getSession(): Long?
       fun clearSession()
       fun isSessionValid(): Boolean
   }
   ```

---

### 2.5 UI Improvements (Without Changing Layout)

1. Add loading overlay on login/register buttons instead of separate indicator
2. Add input validation indicators (real-time)
3. Add "Forgot password?" placeholder link (for future)

---

## PART 3: SUMMARY

### Critical Actions Before Release:
1. Fix race condition in auth initialization (BUG-002)
2. Add logout functionality (BUG-003)
3. Remove unused variables to clear compiler warnings (BUG-004, BUG-005)

### Security Improvements (Recommended for next release):
1. Implement proper password hashing with salt (BUG-001)
2. Use EncryptedSharedPreferences (BUG-006)
3. Add brute force protection (BUG-008)

### Code Quality:
- Total bugs found: 12
- Critical: 2
- High: 4
- Medium: 4
- Low: 2

---

---

## PART 4: FIXES APPLIED

### Fixed in v1.4.0:

| Bug ID | Status | Fix Description |
|--------|--------|-----------------|
| BUG-002 | FIXED | Removed init block race condition in AuthRepository |
| BUG-003 | FIXED | Added logout button in Settings with confirmation dialog |
| BUG-004 | FIXED | Removed unused isLoggedIn variable from MainActivity |
| BUG-005 | FIXED | Removed unused passwordVisible in RegisterScreenContent |
| BUG-011 | FIXED | Removed unused KeyboardOptions imports |

### Remaining for Future Releases:

| Bug ID | Priority | Notes |
|--------|----------|-------|
| BUG-001 | P1 | Requires bcrypt/PBKDF2 implementation |
| BUG-006 | P1 | Requires EncryptedSharedPreferences migration |
| BUG-007 | P2 | Username validation enhancement |
| BUG-008 | P2 | Brute force protection |
| BUG-009 | P2 | Stronger password policy |
| BUG-010 | P3 | Auto-redirect to register if no users |
| BUG-012 | P3 | Preview code duplication cleanup |

---

**Report prepared by:**
- <Senior QA Engineer> - Testing and Bug Report
- <Senior Android Developer> - Code Review and Refactoring Plan

**Date:** 2025-12-20
**Version:** 1.4.0
