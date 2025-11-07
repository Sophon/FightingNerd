package io.github.sophon.wikiwavu.domain.model

import io.github.sophon.core.domain.model.Character
import io.github.sophon.core.domain.model.Move

data class CharacterMoveList(
    val character: Character,
    val moveList: List<Move>,
)
