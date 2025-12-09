package com.uk.ac.tees.mad.lifehacks.presentation.profile

import android.net.Uri

sealed interface ProfileAction {
    object OnEditProfile : ProfileAction
    object OnNotificationTimeClick : ProfileAction
    object OnPreferredCategoriesClick : ProfileAction
    object OnAppThemeClick : ProfileAction
    data class OnBiometricUnlockToggled(val isEnabled: Boolean) : ProfileAction
    object OnLogout : ProfileAction
    data class OnProfilePictureClick(val uri: Uri?) : ProfileAction
}
