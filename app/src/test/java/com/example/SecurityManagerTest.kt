package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.auth.SecurityManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecurityManagerTest {

    private lateinit var securityManager: SecurityManager
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("trisakti_security_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        securityManager = SecurityManager(context)
        securityManager.logout() // start with clean slate
    }

    @Test
    fun `strictly only two authorized accounts exist`() {
        assertEquals(2, SecurityManager.AUTHORIZED_USERS.size)
        val emails = SecurityManager.AUTHORIZED_USERS.map { it.email }
        assertTrue(emails.contains("bajgaisandesh8@gmail.com"))
        assertTrue(emails.contains("arjunbajgai@trisakti.com"))

        val names = SecurityManager.AUTHORIZED_USERS.map { it.name }
        assertTrue(names.contains("Sandesh Bajgai"))
        assertTrue(names.contains("Arjun Prasad Bajgai"))
    }

    @Test
    fun `unauthorized user is rejected immediately`() {
        assertFalse(securityManager.isAuthorized("hacker@malicious.com"))
        assertFalse(securityManager.isAuthorized("random_user@gmail.com"))
        assertFalse(securityManager.isAuthorized("public_user"))
        assertFalse(securityManager.isAuthorized(""))

        val result = securityManager.authenticate("hacker@malicious.com", "any_password")
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials or unauthorized account.", result.exceptionOrNull()?.message)
        assertNull(securityManager.getActiveSession())
    }

    @Test
    fun `sandesh account authenticates with initial password 2009 and requires password change`() {
        val result = securityManager.authenticate("bajgaisandesh8@gmail.com", "2009")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Sandesh Bajgai", user?.name)
        assertEquals("bajgaisandesh8@gmail.com", user?.email)
        assertTrue(user?.requiresPasswordChange == true)

        // Verify active session persists
        val activeSession = securityManager.getActiveSession()
        assertNotNull(activeSession)
        assertEquals("bajgaisandesh8@gmail.com", activeSession?.email)
        assertTrue(activeSession?.requiresPasswordChange == true)
    }

    @Test
    fun `sandesh account quick id authenticates with initial password 2009`() {
        val result = securityManager.authenticate("sandesh", "2009")
        assertTrue(result.isSuccess)
        assertEquals("Sandesh Bajgai", result.getOrNull()?.name)
    }

    @Test
    fun `sandesh account fails with incorrect password`() {
        val result = securityManager.authenticate("bajgaisandesh8@gmail.com", "wrong_password")
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials or unauthorized account.", result.exceptionOrNull()?.message)
        assertNull(securityManager.getActiveSession())
    }

    @Test
    fun `arjun prasad bajgai account authenticates with initial password 1234 and requires password change`() {
        val result = securityManager.authenticate("arjunbajgai@trisakti.com", "1234")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Arjun Prasad Bajgai", user?.name)
        assertEquals("arjunbajgai@trisakti.com", user?.email)
        assertTrue(user?.requiresPasswordChange == true)

        val activeSession = securityManager.getActiveSession()
        assertNotNull(activeSession)
        assertEquals("arjunbajgai@trisakti.com", activeSession?.email)
        assertTrue(activeSession?.requiresPasswordChange == true)
    }

    @Test
    fun `arjun account quick id authenticates with initial password 1234`() {
        val result = securityManager.authenticate("arjun", "1234")
        assertTrue(result.isSuccess)
        assertEquals("Arjun Prasad Bajgai", result.getOrNull()?.name)
    }

    @Test
    fun `arjun account fails with incorrect password`() {
        val result = securityManager.authenticate("arjunbajgai@trisakti.com", "wrong_pwd")
        assertTrue(result.isFailure)
        assertEquals("Invalid credentials or unauthorized account.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `password change updates credentials, clears required flag, and invalidates old password`() {
        // 1. Log in Arjun with initial password 1234
        val loginResult = securityManager.authenticate("arjun", "1234")
        assertTrue(loginResult.isSuccess)
        assertTrue(securityManager.getActiveSession()?.requiresPasswordChange == true)

        // 2. Change password to new secure password
        val changeResult = securityManager.changePassword("arjun", "1234", "SecureArjun9988!")
        assertTrue(changeResult.isSuccess)
        assertFalse(securityManager.requiresPasswordChange("arjun"))

        // 3. Active session is updated
        assertFalse(securityManager.getActiveSession()!!.requiresPasswordChange)

        // 4. Log out
        securityManager.logout()
        assertNull(securityManager.getActiveSession())

        // 5. Old initial password 1234 MUST NO LONGER WORK!
        val oldAttempt = securityManager.authenticate("arjun", "1234")
        assertTrue("Old initial password must be rejected after change", oldAttempt.isFailure)
        assertNull(securityManager.getActiveSession())

        // 6. New password MUST WORK!
        val newAttempt = securityManager.authenticate("arjun", "SecureArjun9988!")
        assertTrue("New password must successfully authenticate", newAttempt.isSuccess)
        assertFalse(newAttempt.getOrNull()!!.requiresPasswordChange)
    }

    @Test
    fun `cannot change password with incorrect current password`() {
        securityManager.authenticate("sandesh", "2009")
        val failResult = securityManager.changePassword("sandesh", "wrong_current", "NewPassword2026!")
        assertTrue(failResult.isFailure)
        assertEquals("Current password is incorrect.", failResult.exceptionOrNull()?.message)
    }

    @Test
    fun `logout completely wipes active session`() {
        securityManager.authenticate("sandesh", "2009")
        assertNotNull(securityManager.getActiveSession())

        securityManager.logout()
        assertNull(securityManager.getActiveSession())
    }
}
