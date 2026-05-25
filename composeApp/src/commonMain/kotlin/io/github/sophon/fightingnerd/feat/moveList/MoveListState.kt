package io.github.sophon.fightingnerd.feat.moveList

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.fightingnerd.feat.moveList.model.Property

internal data class MoveListState(
    val character: Character?,
    val game: Game?,
    val moveList: Map<String, Move> = emptyMap(),

    val uiMoveList: List<UiMove> = emptyList(),
    val moveDetail: MoveDetail? = null,
) {
    data class UiMove(
        val id: String,

        val startup: String,
        val level: String,
        val propertySet: Set<Property> = emptySet(),

        val onHit: String,
        val onBlock: String,
        val onCounter: String,
    )

    data class MoveDetail(
        val move: Move,
    )
}
