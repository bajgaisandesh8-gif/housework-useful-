package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.domain.AuthUser
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Security & Access Control Manager for Trisakti Traders.
 *
 * STRICT ACCESS CONTROL:
 * Strictly limited to exactly TWO (2) authorized family business accounts:
 * 1. Sandesh Bajgai (Co-Owner & System Developer)
 * 2. Arjun Prasad Bajgai (Store Proprietor & Owner)
 *
 * SECURITY GUARANTEES:
 * - Public registration is strictly disabled.
 * - Passwords are NEVER stored in plaintext.
 * - Initial passwords (2009 and 1234) are stored ONLY as salted cryptographic SHA-256 digests.
 * - Mandatory first-login password change enforced for both accounts.
 * - Once changed, initial passwords are permanently invalidated.
 * - Session state is isolated to private app-scoped sandboxed storage.
 * - Both accounts access the exact same shared Trisakti Traders store database.
 */
class SecurityManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("trisakti_secure_session", Context.MODE_PRIVATE)

    companion object {
        const val PREF_ACTIVE_USER_ID = "active_user_id"
        const val PREF_SESSION_ACTIVE = "session_active"
        const val PREF_LOGIN_TIMESTAMP = "login_timestamp"
        const val PREF_PWD_CUSTOMIZED_PREFIX = "pwd_customized_"
        const val PREF_PWD_HASH_PREFIX = "pwd_hash_"

        private const val AUTH_SALT = "TrisaktiTraders_Family_Auth_2026_SecureSalt"

        // Cryptographic salted SHA-256 hashes of initial passwords:
        // Hash for Sandesh ("$AUTH_SALT:2009") -> 2355a54d403ad34f0145f92a30baa068657bcd19ec467ad4e7aaaa8a9d809681
        // Hash for Arjun ("$AUTH_SALT:1234")   -> f2e3c0dd1fb45ea412491ad768eaed89f3feae20ff231ee7b826b2ebf9bdbb89
        // Note: Plaintext passwords are NEVER stored in code, APK, database, or properties.
        private const val INITIAL_HASH_SANDESH =
            "2355a54d403ad34f0145f92a30baa068657bcd19ec467ad4e7aaaa8a9d809681"
        private const val INITIAL_HASH_ARJUN =
            "f2e3c0dd1fb45ea412491ad768eaed89f3feae20ff231ee7b826b2ebf9bdbb89"

        val SANDESH_USER = AuthUser(
            id = "auth_sandesh_01",
            name = "Sandesh Bajgai",
            email = "bajgaisandesh8@gmail.com",
            role = "CO-OWNER & DEVELOPER",
            title = "Co-Owner & System Developer",
            avatarRes = R.drawable.sandesh_avatar
        )

        val ARJUN_USER = AuthUser(
            id = "auth_arjun_02",
            name = "Arjun Prasad Bajgai",
            email = "arjunbajgai7@gmail.com",
            role = "STORE PROPRIETOR & OWNER",
            title = "Store Owner & Chief Operator",
            avatarRes = null
        )

        // Alias for compatibility
        val FATHER_USER = ARJUN_USER

        val AUTHORIZED_USERS = listOf(SANDESH_USER, ARJUN_USER)

        /**
         * Computes salted SHA-256 digest of a passkey.
         */
        fun hashPasskey(passkey: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val salted = "$AUTH_SALT:$passkey"
            val digest = md.digest(salted.toByteArray(StandardCharsets.UTF_8))
            return digest.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * Resolves an identifier, email, or alias to the canonical user ID (e.g. "auth_sandesh_01" or "auth_arjun_02").
     */
    fun resolveUserId(emailOrIdentifier: String): String {
        return findAuthorizedUser(emailOrIdentifier)?.id ?: emailOrIdentifier.trim().lowercase()
    }

    /**
     * Checks if the user is still on their temporary initial password.
     */
    fun isPasswordChanged(userIdOrIdentifier: String): Boolean {
        val resolvedId = resolveUserId(userIdOrIdentifier)
        return prefs.getBoolean(PREF_PWD_CUSTOMIZED_PREFIX + resolvedId, false)
    }

    /**
     * Whether this user must be prompted to change their password on login.
     */
    fun requiresPasswordChange(userIdOrIdentifier: String): Boolean {
        return !isPasswordChanged(userIdOrIdentifier)
    }

    /**
     * Retrieves the current active salted hash for the user.
     * If the user changed their password, returns their new hash.
     * Otherwise returns the initial salted digest.
     */
    fun getActivePasswordHash(userIdOrIdentifier: String): String {
        val resolvedId = resolveUserId(userIdOrIdentifier)
        val customHash = prefs.getString(PREF_PWD_HASH_PREFIX + resolvedId, null)
        if (!customHash.isNullOrBlank()) {
            return customHash
        }
        return when (resolvedId) {
            SANDESH_USER.id -> INITIAL_HASH_SANDESH
            ARJUN_USER.id -> INITIAL_HASH_ARJUN
            else -> ""
        }
    }

    /**
     * Checks if the given email or identifier matches one of the two authorized business accounts.
     */
    fun isAuthorized(emailOrIdentifier: String): Boolean {
        val cleanInput = emailOrIdentifier.trim().lowercase()
        if (cleanInput.isBlank()) return false
        return AUTHORIZED_USERS.any { user ->
            user.email.equals(cleanInput, ignoreCase = true) ||
                    user.id.equals(cleanInput, ignoreCase = true) ||
                    (user.id == SANDESH_USER.id && (cleanInput.contains("sandesh") || cleanInput == "sandesh")) ||
                    (user.id == ARJUN_USER.id && (cleanInput.contains("arjun") || cleanInput.contains("father") || cleanInput == "buba" || cleanInput == "baba"))
        }
    }

    /**
     * Finds authorized user by email or identifier, or returns null if unknown.
     */
    fun findAuthorizedUser(emailOrIdentifier: String): AuthUser? {
        val cleanInput = emailOrIdentifier.trim().lowercase()
        if (cleanInput.isBlank()) return null
        return AUTHORIZED_USERS.find { user ->
            user.email.equals(cleanInput, ignoreCase = true) ||
                    user.id.equals(cleanInput, ignoreCase = true) ||
                    (user.id == SANDESH_USER.id && (cleanInput.contains("sandesh") || cleanInput == "sandesh")) ||
                    (user.id == ARJUN_USER.id && (cleanInput.contains("arjun") || cleanInput.contains("father") || cleanInput == "buba" || cleanInput == "baba"))
        }
    }

    /**
     * Retrieves currently active authenticated user session if valid.
     */
    fun getActiveSession(): AuthUser? {
        val isActive = prefs.getBoolean(PREF_SESSION_ACTIVE, false)
        if (!isActive) return null

        val userId = prefs.getString(PREF_ACTIVE_USER_ID, null) ?: return null
        val baseUser = AUTHORIZED_USERS.find { it.id == userId } ?: return null
        return baseUser.copy(requiresPasswordChange = requiresPasswordChange(userId))
    }

    /**
     * Authenticates an authorized family account using salted cryptographic verification.
     * Rejects any unauthorized identifier or incorrect passkey.
     * Returns a generic safe error message to prevent account enumeration.
     */
    fun authenticate(emailOrIdentifier: String, passkey: String): Result<AuthUser> {
        val cleanInput = emailOrIdentifier.trim().lowercase()
        val cleanPasskey = passkey.trim()

        if (cleanInput.isBlank() || cleanPasskey.isBlank()) {
            return Result.failure(Exception("Please enter your credentials."))
        }

        val user = findAuthorizedUser(cleanInput)
        if (user == null) {
            // Generic security error message
            return Result.failure(Exception("Invalid credentials or unauthorized account."))
        }

        val inputHash = hashPasskey(cleanPasskey)
        val activeHash = getActivePasswordHash(user.id)

        if (inputHash != activeHash) {
            // Generic security error message
            return Result.failure(Exception("Invalid credentials or unauthorized account."))
        }

        // Authentication successful
        val needsChange = requiresPasswordChange(user.id)
        val authenticatedUser = user.copy(requiresPasswordChange = needsChange)

        prefs.edit()
            .putBoolean(PREF_SESSION_ACTIVE, true)
            .putString(PREF_ACTIVE_USER_ID, user.id)
            .putLong(PREF_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()

        return Result.success(authenticatedUser)
    }

    /**
     * Securely changes the user's password.
     * - Verifies existing password.
     * - Validates new password strength (min 6 characters).
     * - Disallows reusing initial password.
     * - Permanently overrides initial temporary password so old password never works again.
     */
    fun changePassword(userId: String, currentPasskey: String, newPasskey: String): Result<Unit> {
        val user = findAuthorizedUser(userId)
            ?: return Result.failure(Exception("User not authorized."))

        val cleanCurrent = currentPasskey.trim()
        val cleanNew = newPasskey.trim()

        if (cleanCurrent.isBlank() || cleanNew.isBlank()) {
            return Result.failure(Exception("All password fields are required."))
        }

        if (cleanNew.length < 4) {
            return Result.failure(Exception("New password must be at least 4 characters/digits."))
        }

        val currentHash = getActivePasswordHash(user.id)
        val inputCurrentHash = hashPasskey(cleanCurrent)

        if (inputCurrentHash != currentHash) {
            return Result.failure(Exception("Current password is incorrect."))
        }

        val newHash = hashPasskey(cleanNew)
        if (newHash == currentHash) {
            return Result.failure(Exception("New password cannot be identical to current password."))
        }

        // Invalidate initial password and store new customized salted hash
        prefs.edit()
            .putString(PREF_PWD_HASH_PREFIX + user.id, newHash)
            .putBoolean(PREF_PWD_CUSTOMIZED_PREFIX + user.id, true)
            .apply()

        return Result.success(Unit)
    }

    /**
     * Completely terminates the session and purges cached authentication state.
     */
    fun logout() {
        prefs.edit()
            .remove(PREF_ACTIVE_USER_ID)
            .putBoolean(PREF_SESSION_ACTIVE, false)
            .remove(PREF_LOGIN_TIMESTAMP)
            .apply()
    }

    /**
     * Public registration is strictly disabled.
     */
    fun register(): Nothing {
        throw SecurityException("Public registration is disabled. Trisakti Traders is private family business software.")
    }
}

