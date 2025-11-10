package com.arjun.lifehacks.presentation.auth.create_account

sealed class CreateAccountEvent {
    data object Success: CreateAccountEvent()
    data object Failure: CreateAccountEvent()
    data object GoToLogin: CreateAccountEvent()
}