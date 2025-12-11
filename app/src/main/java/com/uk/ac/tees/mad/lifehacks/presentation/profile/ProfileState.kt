package com.uk.ac.tees.mad.lifehacks.presentation.profile


data class ProfileState(
    val user: User? = null,
    val notificationTime: String = "",
    val preferredCategories: String = "",
    val appTheme: String = "",
    val isBiometricUnlockEnabled: Boolean = false,
    val appVersion: String = "",
    val areNotificationsEnabled: Boolean = false
)
