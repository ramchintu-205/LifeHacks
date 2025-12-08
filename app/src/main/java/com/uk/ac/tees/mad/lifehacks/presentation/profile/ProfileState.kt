package com.uk.ac.tees.mad.lifehacks.presentation.profile


data class ProfileState(
    val user: User? = null,
    val notificationTime: String = "",
    val preferredCategories: String = "",
    val appTheme: String = "",
    val isBiometricUnlockEnabled: Boolean = false,
    val appVersion: String = ""
)

// This should ideally be in its own file in the 'domain' package
// e.g. src/main/java/com/uk/ac/tees/mad/lifehacks/domain/User.kt
data class User(
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null
)
