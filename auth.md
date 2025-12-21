# Auth Module - Release QA & Refactoring Plan

**Module:** Authorization & Registration
**Version:** 1.4.7
**Date:** 2025-12-21
**Author:** <Senior QA Engineer>, <Senior Android Developer>

---

## PART 1: QA TESTING REPORT (<Senior QA Engineer>)

### 1.1 Test Approach
- Code-based inspection and logic walkthrough (no UI/visual testing).
- Reviewed: `AuthRepository`, `AuthViewModel`, `LoginScreen`, `RegisterScreen`, `MainActivity` session init, and logout flow in `SettingsScreen`.

### 1.2 Regression Test Plan (Release)

| Area | Coverage | Priority | Notes |
|------|----------|----------|-------|
| Login | Valid/invalid credentials, inactive user, empty fields | Critical | Core auth entry |
| Registration | Validation, duplicates, password mismatch, whitespace | Critical | First-time access |
| Session | Restore, logout, invalid session cleanup | High | Access control |
| Password | Change password logic, minimum length | High | Security / account safety |
| Profile | Update profile data, state update | Medium | Settings path |
| Data Persistence | Upgrade behavior, session storage | High | Data loss risk |
| Security | Hashing, storage of session data | Critical | Security baseline |

### 1.3 Test Cases & Results

| Test Case | Result | Evidence/Notes |
|-----------|--------|----------------|
| Login with valid credentials | PASS | `AuthRepository.login()` handles success path |
| Login with wrong password | PASS | Error on hash mismatch (`AuthRepository.kt:63-66`) |
| Login with inactive user | PASS | Explicit check (`AuthRepository.kt:59-60`) |
| Login with empty username/password | PASS | Blank checks (`AuthRepository.kt:49-54`) |
| Registration with valid data | PASS | Inserts user + auto-login (`AuthRepository.kt:121-130`) |
| Registration with existing username | PASS | `isUsernameExists` gate (`AuthRepository.kt:109-111`) |
| Registration password mismatch | PASS | Validation (`AuthRepository.kt:100-101`) |
| Registration with username "  ab " | FAIL | Length check before trim allows <3 after trim (AUTH-003) |
| Display name is blank | PASS | Validation (`AuthRepository.kt:103-105`) |
| Session restored on app start | PASS | `initializeSession()` (`AuthRepository.kt:35-45`) |
| Logout clears session | PASS | `logout()` + `clearSession()` (`AuthRepository.kt:136-139`) |
| Upgrade preserves users | FAIL | Destructive migration enabled (AUTH-004) |
| Session storage encrypted | FAIL | Plain SharedPreferences (AUTH-002) |
| Password hashing uses salt | FAIL | SHA-256 without salt (AUTH-001) |

### 1.4 Bug Report (QA)

#### AUTH-001: Password hashing without salt
- **Severity:** Critical
- **Location:** `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:199`
- **Description:** Passwords are hashed with SHA-256 without per-user salt.
- **Impact:** Vulnerable to rainbow table / offline cracking if DB is leaked.
- **Recommendation:** Use PBKDF2/bcrypt/Argon2 with unique salt per user.

#### AUTH-002: Session stored in plain SharedPreferences
- **Severity:** High
- **Location:** `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:191-196`
- **Description:** `KEY_USER_ID` stored unencrypted in shared prefs.
- **Impact:** Session can be extracted on rooted devices or via backups.
- **Recommendation:** Use `EncryptedSharedPreferences` or `DataStore` with encryption.

#### AUTH-003: Username length check ignores trim
- **Severity:** Medium
- **Location:** `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:88-107`
- **Description:** Length check runs before `trim()`, so `"  ab "` passes length >= 3 but becomes `"ab"` (length 2) when saved.
- **Impact:** Allows usernames below minimum length; inconsistent validation.
- **Recommendation:** Normalize first, then validate length.

#### AUTH-004: Destructive DB migration wipes users
- **Severity:** High
- **Location:** `app/src/main/java/com/agrodiary/di/DatabaseModule.kt:31-37`
- **Description:** `fallbackToDestructiveMigration()` will delete user accounts on schema changes.
- **Impact:** Data loss on upgrade; users lose accounts and sessions.
- **Recommendation:** Provide a migration path from DB v1 -> v2 (users table included).

---

## PART 2: DEVELOPER REVIEW (<Senior Android Developer>)

### 2.1 Additional Issues Found (added to bug report)

#### DEV-001: Internal exception messages exposed to UI
- **Severity:** Medium
- **Location:** `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:121-132`, `:142-149`
- **Description:** Raw `Exception.message` is returned in `AuthResult.Error`.
- **Impact:** Leaks internal details to users; inconsistent UX.
- **Recommendation:** Log technical details, return a stable user-facing message.

#### DEV-002: In-memory user state not aligned with DB update timestamp
- **Severity:** Low
- **Location:** `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:142-146`
- **Description:** `updateProfile()` writes `updatedAt` to DB but `_currentUser` keeps old `updatedAt`.
- **Impact:** `currentUser` state may be stale for consumers relying on timestamps.
- **Recommendation:** Update `_currentUser` with the same `updatedAt` value used in DB.

### 2.2 Refactoring & Optimization Plan (No UI Changes)

| Priority | Task | Rationale | Risk |
|----------|------|-----------|------|
| P0 | Implement salted, slow hashing (PBKDF2/bcrypt/Argon2) | Security baseline | Medium |
| P0 | Replace plain prefs with encrypted storage | Protect sessions | Low |
| P0 | Add explicit Room migration for users | Prevent data loss | Medium |
| P1 | Normalize input before validation | Fix AUTH-003 | Low |
| P1 | Extract `AuthResult` to domain layer | Reduce coupling | Low |
| P1 | Add `SessionManager` abstraction | Centralize session logic | Low |
| P2 | Move error strings to resources | Consistent i18n | Low |
| P2 | Add unit tests for AuthRepository/AuthViewModel | Prevent regressions | Medium |

### 2.3 Unused / Dead Code

1. `app/src/main/java/com/agrodiary/data/local/dao/UserDao.kt:25` - `getLastActiveUser()` unused
2. `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:152-183` - `changePassword()` not called from UI
3. `app/src/main/java/com/agrodiary/data/repository/AuthRepository.kt:185-189` - `hasUsers()` / `getAllUsers()` not used by UI
4. `app/src/main/java/com/agrodiary/ui/auth/AuthViewModel.kt:50-62` - `hasUsers` state not consumed in screens

---

## SUMMARY

- **Bugs found:** 6 (Critical: 1, High: 2, Medium: 2, Low: 1)
- **Highest risk:** password hashing and destructive DB migration
- **Release readiness:** requires P0 items before production release

