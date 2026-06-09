package io.github.sophon.fightingnerd.feat.moveList.ui

import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.moveList.model.Property

internal data class MoveListState(
    val character: Character?,
    val fullMoveList: Map<String, Move> = emptyMap(),

    val uiMoveList: List<UiMove> = emptyList(),
    val moveDetail: MoveDetail? = null,
) {
    data class UiMove(
        val id: String,
        val input: String,

        val startup: String?,
        val level: String?,
        val propertySet: Set<Property> = emptySet(),

        val onHit: String?,
        val onBlock: String?,
        val onCounter: String?,
    )

    data class MoveDetail(
        val move: Move,
    )


    companion object {
        val armorKingMoves = listOf(
            Move(
                characterId = "Armor King",
                id = "armor_king-b12",
                name = "Dark Jab > Hell Stab",
                input = "b12",
                damage = "12, 20",
                startup = "i12~13 (i20~21)",
                onBlock = "-12",
                onHit = "+8",
                onCH = "+11",
                recovery = "30",
                guard = "h, m",
                notes = listOf(
                    "Combo from 1st hit with 12F delay",
                    "Transition to r30 BAD with F (-12/+8/+11)",
                    "Move can be delayed by 10F",
                    "Input can be delayed by 12F",
                ),
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-b+1,2",
                ),
                t8Properties = Move.T8Properties(),
            ),
            Move(
                characterId = "Armor King",
                id = "armor_king-b1+2",
                name = "Blindside",
                input = "b1+2",
                damage = "0",
                startup = "i16~17",
                onBlock = "+0",
                onHit = "+4",
                onCH = "+15",
                recovery = "r27",
                guard = "m",
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-b+1+2",
                ),
                t8Properties = Move.T8Properties(),
            ),
            Move(
                characterId = "Armor King",
                id = "armor_king-h.ub1",
                name = "Neck Hunter: Villain",
                input = "h.ub1",
                damage = "25",
                startup = "i24~25",
                onBlock = "+8",
                onHit = "+60a",
                recovery = "r25",
                guard = "h",
                notes = listOf(
                    "Strong Aerial Tailspin",
                    "Homing",
                    "Transition to r26 BAD with F (+8/+61a)",
                    "Consumes 150F of remaining Heat time",
                    "7 chip damage on block",
                ),
                urls = Move.Urls(
                    wikiUrl = "https://wavu.wiki/t/Armor_King_movelist#Armor_King-H.ub+1",
                ),
                t8Properties = Move.T8Properties(isHeat = true, isHoming = true),
            ),
        )

        val PREVIEW = MoveListState(
            character = null,
            fullMoveList = armorKingMoves.associateBy { it.id },
            uiMoveList = armorKingMoves.map { it.toUiMove() },
            moveDetail = null,
        )

        private fun Move.toUiMove(): UiMove {
            return UiMove(
                id = id,
                input = input,
                startup = startup,
                level = guard,
                onHit = onHit,
                onBlock = onBlock,
                onCounter = onCH,
                propertySet = buildSet {
                    invulnerability?.let { add(Property.Invincible) }
                    t8Properties?.let { props ->
                        if (props.isHeat) add(Property.Heat)
                        if (props.isHoming) add(Property.Homing)
                        if (props.isPowerCrush) add(Property.PowerCrush)
                        if (props.isHighCrush) add(Property.HighCrush)
                        if (props.isLowCrush) add(Property.LowCrush)
                    }
                },
            )
        }
    }
}
