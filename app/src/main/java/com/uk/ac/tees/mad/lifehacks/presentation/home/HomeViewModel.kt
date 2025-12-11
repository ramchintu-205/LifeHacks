package com.uk.ac.tees.mad.lifehacks.presentation.home

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uk.ac.tees.mad.lifehacks.data.AdviceSlipRepository
import com.uk.ac.tees.mad.lifehacks.domain.util.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class HomeNavEvent {
    data object NavigateToFavourites : HomeNavEvent()
    data object NavigateToSettings : HomeNavEvent()
}

class HomeViewModel(
    private val adviceSlipRepository: AdviceSlipRepository
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadRandomLifeHack()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState(isLoading = true) // Show loading initially
        )

    private val _navigationEvent = MutableSharedFlow<HomeNavEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.FavoriteClicked -> saveCurrentLifeHackAsFavorite()
            HomeAction.NewTipClicked -> loadRandomLifeHack()
            HomeAction.AddPhotoClicked -> Log.d("HomeViewModel", "Add Photo clicked")
            HomeAction.ShareClicked -> Log.d("HomeViewModel", "Share clicked")
            HomeAction.TodayTabClicked -> {}
            HomeAction.SavedTabClicked -> viewModelScope.launch {
                _navigationEvent.emit(HomeNavEvent.NavigateToFavourites)
            }
            HomeAction.SettingsTabClicked -> viewModelScope.launch {
                _navigationEvent.emit(HomeNavEvent.NavigateToSettings)
            }
            is HomeAction.OnImageCaptured -> {
                _state.update {
                    it.copy(lifeHack = it.lifeHack?.copy(imageUrl = action.uri.toString()))
                }
            }
            HomeAction.OnImageCleared -> {  // Add this
                _state.update {
                    it.copy(lifeHack = it.lifeHack?.copy(imageUrl = null))
                }
            }
        }
    }

    private fun saveCurrentLifeHackAsFavorite() {
        viewModelScope.launch {
            state.value.lifeHack?.let {
                adviceSlipRepository.saveAsFavorite(it)
                _state.update {
                    it.copy(lifeHack = it.lifeHack?.copy(isFavorite = true))
                }
            }
        }
    }

    private fun loadRandomLifeHack() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val favoritesFlow = adviceSlipRepository.getFavoritesFromCache()
            when (val result = adviceSlipRepository.getRandomLifeHack()) {
                is Result.Success -> {
                    favoritesFlow.collect { favorites ->
                        val isFavorite = favorites.any { it.title == result.data.title }
                        _state.update {
                            it.copy(
                                lifeHack = result.data.copy(isFavorite = isFavorite),
                                isLoading = false
                            )
                        }
                    }
                }
                is Result.Failure -> _state.update { it.copy(error = "Failed to load hack", isLoading = false) }
            }
        }
    }
}
