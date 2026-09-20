package com.example.domain

/**
 * Represents an authenticated family business user for Trisakti Traders.
 * Only two authorized accounts exist:
 * 1. Sandesh Bajgai (Co-Owner & System Developer)
 * 2. Arjun Prasad Bajgai (Store Proprietor & Owner)
 */
data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val title: String,
    val avatarRes: Int? = null,
    val requiresPasswordChange: Boolean = false
)
