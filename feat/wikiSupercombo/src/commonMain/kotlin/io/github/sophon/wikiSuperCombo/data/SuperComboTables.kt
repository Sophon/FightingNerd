package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.wiki.data.QueryTable

internal object SuperComboTables {
    private val gameToTables = mapOf(
        "Street_Fighter_6" to QueryTable(
            character = TABLE_SF6_CHARACTERS,
            moves = TABLE_SF6_MOVE_LIST
        ),
//        "Mortal_Kombat_1" to Tables(
//            character = TABLE_MK1_CHARACTERS,
//            moves = TABLE_MK1_MOVE_LIST
//        ),
//        "Soulcalibur_6" to Tables(
//            character = TABLE_SC6_CHARACTERS,
//            moves = TABLE_SC6_MOVE_LIST
//        )
    )

    fun getTable(gameId: String): QueryTable? {
        return gameToTables[gameId]
    }

    fun supportedGames(): Set<String> = gameToTables.keys
}

private const val TABLE_SF6_CHARACTERS = "SF6_Characters"
private const val TABLE_SF6_MOVE_LIST = "SF6_FrameData"
