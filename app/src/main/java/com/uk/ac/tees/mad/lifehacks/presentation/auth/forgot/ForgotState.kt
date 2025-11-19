package com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot

data class ForgotState(
    val email: String = "",
    val isLoading: Boolean = false
)