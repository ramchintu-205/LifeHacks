package com.uk.ac.tees.mad.lifehacks.presentation.profile

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null
)
