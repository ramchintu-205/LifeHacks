package com.uk.ac.tees.mad.lifehacks.domain.util

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
}
