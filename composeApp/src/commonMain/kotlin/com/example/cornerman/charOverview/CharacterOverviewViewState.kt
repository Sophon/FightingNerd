package com.example.cornerman.charOverview

import org.jetbrains.compose.resources.StringResource

data class CharacterOverviewViewState(
    val charName: String,
    val moveList:,

    val isLoading: Boolean = false,
    val error: StringResource? = null,
) {
    data class InputCategory(
        val categoryName: String,
        moveList: List<Move>
    )
}
