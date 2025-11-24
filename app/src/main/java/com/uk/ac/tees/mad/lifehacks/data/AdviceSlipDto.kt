package com.uk.ac.tees.mad.lifehacks.data

import kotlinx.serialization.Serializable

@Serializable
data class AdviceSlipResponse(
    val slip: Slip
)

@Serializable
data class Slip(
    val id: Int,
    val advice: String
)
