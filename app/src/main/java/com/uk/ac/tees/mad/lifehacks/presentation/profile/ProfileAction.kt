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
    data class OnNotificationToggled(val isEnabled: Boolean) : ProfileAction
    object OnNotificationClick : ProfileAction
    data class OnNotificationTimeChange(val hour: Int, val minute: Int) : ProfileAction
}
