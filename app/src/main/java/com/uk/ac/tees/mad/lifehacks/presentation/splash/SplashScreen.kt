package com.uk.ac.tees.mad.lifehacks.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uk.ac.tees.mad.lifehacks.presentation.navigation.GraphRoutes
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashRoot(
    viewModel: SplashViewModel = koinViewModel(),
    onNavigation: (GraphRoutes) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state.isUserAuthenticated != null) {
            val route = if (state.isUserAuthenticated == true) GraphRoutes.Home else GraphRoutes.Login
            onNavigation(route)
        }
    }

    SplashScreen(state = state)
}

@Composable
fun SplashScreen(state: SplashState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Splash Screen Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.cachedHack != null) {
                Text(text = state.cachedHack.title)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isOffline) {
                Text(text = "You are offline. Showing cached content.")
                Spacer(modifier = Modifier.height(16.dp))
            }

            CircularProgressIndicator()
        }
    }
}
