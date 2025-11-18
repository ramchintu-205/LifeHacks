package com.arjun.lifehacks.presentation.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.lifehacks.domain.AuthRepository
import com.arjun.lifehacks.domain.util.onFailure
import com.arjun.lifehacks.domain.util.onSuccess
import com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot.ForgotAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ForgotState())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ForgotState()
    )

    private val eventChannel = Channel<ForgotEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ForgotAction) {
        when (action) {
            is ForgotAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            ForgotAction.OnSubmitClick -> forgotPassword()
            ForgotAction.OnBackToLoginClick -> sendEvent(ForgotEvent.GoToLogin)
            ForgotAction.OnBackArrowClick -> sendEvent(ForgotEvent.GoToLogin)
        }
    }

    private fun forgotPassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.forgotPassword(state.value.email)
                .onSuccess { sendEvent(ForgotEvent.Success) }
                .onFailure { sendEvent(ForgotEvent.Failure) }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEvent(event: ForgotEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}