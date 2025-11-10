package com.arjun.lifehacks.domain.util

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
}
