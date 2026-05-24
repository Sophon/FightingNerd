package io.github.sophon.fightingnerd.feat.moveList

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move

internal data class MoveListState(
    val character: Character?,
    val game: Game?,
    val moveList: List<Move> = emptyList(),
)
