package com.uk.ac.tees.mad.lifehacks.presentation.navigation

import kotlinx.serialization.Serializable
sealed class GraphRoutes {
    @Serializable
    data object Login : GraphRoutes()
    @Serializable
    data object Register : GraphRoutes()
    @Serializable
    data object Forgot : GraphRoutes()

    @Serializable
    data object Home: GraphRoutes()
}