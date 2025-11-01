package io.github.sophon.cornerman.screens.moveList.domain

import com.example.wikiwavu.domain.model.Move

data class MoveCategory(
    val name: String,
    val moves: List<Move>,
)