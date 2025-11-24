package com.uk.ac.tees.mad.lifehacks.presentation.home

import android.net.Uri

sealed interface HomeAction {
    data object FavoriteClicked : HomeAction
    data object NewTipClicked : HomeAction
    data object AddPhotoClicked : HomeAction
    data object ShareClicked : HomeAction
    data object TodayTabClicked : HomeAction
    data object SavedTabClicked : HomeAction
    data object SettingsTabClicked : HomeAction
    data class OnImageCaptured(val uri: Uri?) : HomeAction
}
