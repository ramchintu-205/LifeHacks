package com.uk.ac.tees.mad.lifehacks.presentation.splash

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uk.ac.tees.mad.lifehacks.data.database.LifeHackDao
import com.uk.ac.tees.mad.lifehacks.data.database.toLifeHack
import com.uk.ac.tees.mad.lifehacks.presentation.home.LifeHack
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SplashState(
    val isUserAuthenticated: Boolean? = null,
    val cachedHack: LifeHack? = null,
    val isOffline: Boolean = false
)

class SplashViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val lifeHackDao: LifeHackDao,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val cachedHack = lifeHackDao.getRandomLifeHack()?.toLifeHack()
            _state.value = _state.value.copy(cachedHack = cachedHack)

            delay(2000) // Simulate a loading process

            val isOffline = !isNetworkAvailable()
            _state.value = _state.value.copy(
                isUserAuthenticated = firebaseAuth.currentUser != null,
                isOffline = isOffline
            )
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
