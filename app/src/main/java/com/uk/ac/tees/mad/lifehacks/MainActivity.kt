package com.uk.ac.tees.mad.lifehacks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.uk.ac.tees.mad.lifehacks.presentation.navigation.Navigation
import com.uk.ac.tees.mad.lifehacks.ui.theme.LifeHacksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            LifeHacksTheme {
                Navigation(
                    navcontroller = navController
                )
            }
        }
    }
}

