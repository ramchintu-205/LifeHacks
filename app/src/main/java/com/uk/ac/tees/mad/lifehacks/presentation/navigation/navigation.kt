package com.uk.ac.tees.mad.lifehacks.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uk.ac.tees.mad.lifehacks.presentation.auth.create_account.CreateAccountRoot
import com.uk.ac.tees.mad.lifehacks.presentation.auth.forgot.ForgotRoot
import com.uk.ac.tees.mad.lifehacks.presentation.auth.login.LoginRoot
import com.uk.ac.tees.mad.lifehacks.presentation.home.HomeRoot
import com.uk.ac.tees.mad.lifehacks.presentation.splash.SplashRoot

@Composable
fun Navigation(navcontroller: NavHostController){
    NavHost(navController = navcontroller, startDestination = GraphRoutes.Splash){

        composable<GraphRoutes.Splash>{
            SplashRoot(onNavigation = {
                navcontroller.navigate(it) {
                    popUpTo(GraphRoutes.Splash) {
                        inclusive = true
                    }
                }
            })
        }

        composable<GraphRoutes.Login>{
         LoginRoot(
             onLoginSuccess = {
                 navcontroller.navigate(GraphRoutes.Home){
                     popUpTo(GraphRoutes.Login) {
                         inclusive = true
                     }
                 }
                              },
             onGoToCreateAccount = { navcontroller.navigate(GraphRoutes.Register) },
             onGoToForgotPassword = { navcontroller.navigate(GraphRoutes.Forgot) }
         )
        }

        composable<GraphRoutes.Register>{
            CreateAccountRoot(
                onSignInClick = {
                    navcontroller.navigate(GraphRoutes.Login) {
                        popUpTo(GraphRoutes.Register) {
                            inclusive = true
                        }
                    }
                },
                onCreateAccountSuccess = {
                    navcontroller.navigate(GraphRoutes.Home) {
                        popUpTo(GraphRoutes.Register) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<GraphRoutes.Forgot>{
            ForgotRoot(
                onBackToLogin = {
                    navcontroller.navigate(GraphRoutes.Login) {
                        popUpTo(GraphRoutes.Forgot) {
                            inclusive = true
                        }
                    }
                }
            )
        }



        composable<GraphRoutes.Home> { HomeRoot() }
        composable<GraphRoutes.Favourites> { Text(text = "Favourites Screen") }
        composable<GraphRoutes.Settings> { Text(text = "Settings Screen") }


    }

}
