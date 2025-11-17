package io.github.sophon.wikiwavu.domain.model

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move

data class CharacterMoveList(
    val character: Character,
    val moveList: List<Move>,
)
