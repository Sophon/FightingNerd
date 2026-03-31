package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.QueryTable

internal object SuperComboTables {
    private val gameToTables = mapOf(
        Game.StreetFighter6.id to QueryTable(
            character = TABLE_SF6_CHARACTERS,
            moves = TABLE_SF6_MOVE_LIST,
        ),
        Game.MK1.id to QueryTable(
            character = TABLE_MK1_CHARACTERS,
            moves = TABLE_MK1_MOVE_LIST,
        ),
        Game.AVL.id to QueryTable(
            character = TABLE_AVL_CHARACTERS,
            moves = TABLE_AVL_MOVE_LIST,
        ),
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    internal const val TABLE_SF6_CHARACTERS = "SF6_CharacterData"
    internal const val TABLE_SF6_MOVE_LIST = "SF6_FrameData"
    internal const val TABLE_MK1_CHARACTERS = "MK1_CharacterData"
    internal const val TABLE_MK1_MOVE_LIST = "MK1_FrameData"
    internal const val TABLE_AVL_CHARACTERS = "AL_CharacterData"
    internal const val TABLE_AVL_MOVE_LIST = "AL_FrameData"
}
