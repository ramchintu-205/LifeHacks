package com.uk.ac.tees.mad.lifehacks.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileState()
        )

    private fun loadInitialData() {
        _state.value = ProfileState(
            user = User(
                name = "Chintan",
                email = "chintan@lifehacks.com",
                profilePictureUrl = "https://images.unsplash.com/photo-1564564321837-a57b7070ac5c?q=80&w=2952&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
            ),
            notificationTime = "09:00 AM",
            preferredCategories = "3 Selected",
            appTheme = "System",
            isBiometricUnlockEnabled = true,
            appVersion = "LifeHacks v1.0"
        )
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnEditProfile -> { /* TODO: Handle profile edit navigation */ }
            ProfileAction.OnNotificationTimeClick -> { /* TODO: Handle notification time change */ }
            ProfileAction.OnPreferredCategoriesClick -> { /* TODO: Handle categories navigation */ }
            ProfileAction.OnAppThemeClick -> { /* TODO: Handle app theme change */ }
            is ProfileAction.OnBiometricUnlockToggled -> {
                _state.update { it.copy(isBiometricUnlockEnabled = action.isEnabled) }
            }
            ProfileAction.OnLogout -> { /* TODO: Handle logout */ }
        }
    }
}
