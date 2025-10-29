package com.example.cornerman

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {

    @Serializable
    object Home

    @Serializable
    data class MoveList(
        val charName: String,
    )
}