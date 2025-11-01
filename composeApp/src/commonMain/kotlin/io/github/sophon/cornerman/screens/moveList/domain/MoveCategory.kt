package io.github.sophon.cornerman.screens.moveList.domain

import io.github.sophon.wikiwavu.domain.model.Move

data class MoveCategory(
    val name: String,
    val moves: List<Move>,
)