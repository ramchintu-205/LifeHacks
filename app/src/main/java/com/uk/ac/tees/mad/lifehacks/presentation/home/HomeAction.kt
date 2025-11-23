package com.uk.ac.tees.mad.lifehacks.presentation.home

sealed interface HomeAction {
    data object FavoriteClicked : HomeAction
    data object NewTipClicked : HomeAction
    data object AddPhotoClicked : HomeAction
    data object ShareClicked : HomeAction
    data object TodayTabClicked : HomeAction
    data object SavedTabClicked : HomeAction
    data object SettingsTabClicked : HomeAction
}
