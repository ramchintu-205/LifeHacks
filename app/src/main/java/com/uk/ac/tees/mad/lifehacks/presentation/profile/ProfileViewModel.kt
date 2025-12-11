package com.uk.ac.tees.mad.lifehacks.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.lifehacks.data.UserRepository
import com.uk.ac.tees.mad.lifehacks.data.notification.NotificationScheduler
import com.uk.ac.tees.mad.lifehacks.domain.AuthRepository
import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileState()
    )

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userRepository.getUserData().collectLatest { user ->
                _state.update { it.copy(user = user) }
            }
        }
        viewModelScope.launch {
            userRepository.syncUser()
        }
    }

    fun onAction(action: ProfileAction, context: Context) {
        val notificationScheduler = NotificationScheduler(context)
        when (action) {
            is ProfileAction.OnProfilePictureClick -> {
                action.uri?.let { uri ->
                    _state.update { it.copy(user = it.user?.copy(profilePictureUrl = uri.toString())) }
                    viewModelScope.launch {
                        val result = userRepository.uploadProfilePicture(uri)
                        if (result is Result.Success) {
                            userRepository.saveProfilePictureUrl(result.data)
                        }
                        // Optionally handle failure case with a UI message
                    }
                }
            }
            is ProfileAction.OnNotificationToggled -> {
                _state.update { it.copy(areNotificationsEnabled = action.isEnabled) }
                if (action.isEnabled) {
                    // Schedule a default notification
                    notificationScheduler.schedule(9, 0)
                } else {
                    notificationScheduler.cancel()
                }
            }
            is ProfileAction.OnNotificationTimeChange -> {
                _state.update { it.copy(notificationTime = "${action.hour}:${action.minute}") }
                notificationScheduler.schedule(action.hour, action.minute)
            }
            is ProfileAction.OnLogout -> {
                viewModelScope.launch {
                    authRepository.logOut()
                }
            }
            else -> {}
        }
    }
}
