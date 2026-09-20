package com.example.data.auth

import com.example.R
import com.example.data.supabase.SupabaseClientProvider
import com.example.domain.AuthUser
import io.github.jan.supabase.auth.Email

/**
 * Supabase-backed authentication for the private shared family business app.
 *
 * The app does not implement local password storage, hashing, registration, or
 * authorization decisions. Supabase Auth verifies credentials and database RLS
 * decides whether an authenticated identity can access business data.
 */
class SecurityManager {
    private val supabase = SupabaseClientProvider.client

    companion object {
        val SANDESH_USER = AuthUser(
            id = "sandesh",
            name = "Sandesh Bajgai",
            email = "bajgaisandesh8@gmail.com",
            role = "CO-OWNER & DEVELOPER",
            title = "Co-Owner & System Developer",
            avatarRes = R.drawable.sandesh_avatar
        )

        val ARJUN_USER = AuthUser(
            id = "arjun",
            name = "Arjun Prasad Bajgai",
            email = "arjunbajgai7@gmail.com",
            role = "STORE PROPRIETOR & OWNER",
            title = "Store Owner & Chief Operator",
            avatarRes = null
        )

        val FATHER_USER = ARJUN_USER
        val AUTHORIZED_USERS = listOf(SANDESH_USER, ARJUN_USER)
    }

    private fun userForEmail(email: String): AuthUser? =
        AUTHORIZED_USERS.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    suspend fun authenticate(emailOrIdentifier: String, passkey: String): Result<AuthUser> {
        val user = AUTHORIZED_USERS.firstOrNull {
            it.email.equals(emailOrIdentifier.trim(), ignoreCase = true) ||
                it.id.equals(emailOrIdentifier.trim(), ignoreCase = true)
        } ?: return Result.failure(Exception("Invalid credentials or unauthorized account."))

        return try {
            supabase.auth.signInWith(Email) {
                email = user.email
                password = passkey
            }
            Result.success(user.copy(requiresPasswordChange = false))
        } catch (_: Throwable) {
            Result.failure(Exception("Invalid credentials or unauthorized account."))
        }
    }

    suspend fun getActiveSession(): AuthUser? {
        val email = supabase.auth.currentSessionOrNull()?.user?.email ?: return null
        return userForEmail(email)
    }

    suspend fun changePassword(newPasskey: String): Result<Unit> {
        if (newPasskey.trim().length < 8) {
            return Result.failure(Exception("New password must be at least 8 characters."))
        }
        return try {
            supabase.auth.updateUser {
                password = newPasskey.trim()
            }
            Result.success(Unit)
        } catch (error: Throwable) {
            Result.failure(Exception(error.message ?: "Unable to change password."))
        }
    }

    suspend fun logout() {
        try {
            supabase.auth.signOut()
        } catch (_: Throwable) {
            // Local session is cleared by the auth client even if the network request fails.
        }
    }

    fun isAuthorized(emailOrIdentifier: String): Boolean =
        AUTHORIZED_USERS.any {
            it.email.equals(emailOrIdentifier.trim(), ignoreCase = true) ||
                it.id.equals(emailOrIdentifier.trim(), ignoreCase = true)
        }

    fun findAuthorizedUser(emailOrIdentifier: String): AuthUser? =
        AUTHORIZED_USERS.firstOrNull {
            it.email.equals(emailOrIdentifier.trim(), ignoreCase = true) ||
                it.id.equals(emailOrIdentifier.trim(), ignoreCase = true)
        }

    fun register(): Nothing =
        throw SecurityException("Public registration is disabled.")
}
