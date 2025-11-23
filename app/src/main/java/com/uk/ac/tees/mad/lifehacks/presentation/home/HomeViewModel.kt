package com.uk.ac.tees.mad.lifehacks.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                // In a real app, you would load data from a repository here
                // For now, we use the default state which has mock data
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeState(isLoading = true) // Show loading initially
        )

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.FavoriteClicked -> Log.d("HomeViewModel", "Favorite clicked")
            HomeAction.NewTipClicked -> Log.d("HomeViewModel", "New Tip clicked")
            HomeAction.AddPhotoClicked -> Log.d("HomeViewModel", "Add Photo clicked")
            HomeAction.ShareClicked -> Log.d("HomeViewModel", "Share clicked")
            HomeAction.TodayTabClicked -> Log.d("HomeViewModel", "Today tab clicked")
            HomeAction.SavedTabClicked -> Log.d("HomeViewModel", "Saved tab clicked")
            HomeAction.SettingsTabClicked -> Log.d("HomeViewModel", "Settings tab clicked")
        }
    }
}
