package com.arjun.lifehacks.presentation.auth.forgot

sealed class ForgotEvent {
    data object Success: ForgotEvent()
    data object Failure: ForgotEvent()
    data object GoToLogin: ForgotEvent()
}