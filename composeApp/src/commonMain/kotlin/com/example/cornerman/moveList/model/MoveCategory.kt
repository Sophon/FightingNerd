package com.example.cornerman.moveList.model

import com.example.wikiwavu.domain.model.Move

data class MoveCategory(
    val name: String,
    val moves: List<Move>,
)