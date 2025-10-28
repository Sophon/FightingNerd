package com.example.cornerman.screens.charOverview

import com.example.wikiwavu.domain.model.Move
import org.jetbrains.compose.resources.StringResource

data class CharacterOverviewViewState(
    val charName: String,
    val moveList: List<InputCategory>,

    val isLoading: Boolean = false,
    val error: StringResource? = null,
) {
    data class InputCategory(
        val categoryName: String,
        val moveList: List<Move>,
    )
}
