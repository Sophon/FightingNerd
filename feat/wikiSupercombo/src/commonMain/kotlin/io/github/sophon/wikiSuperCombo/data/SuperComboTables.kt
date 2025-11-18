package io.github.sophon.wikiSuperCombo.data

internal object SuperComboTables {
    private val gameToTables = mapOf(
        "Street_Fighter_6" to Tables(
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

    internal data class Tables(
        val character: String,
        val moves: String
    )

    fun getTable(gameId: String): Tables? {
        return gameToTables[gameId]
    }

    fun supportedGames(): Set<String> = gameToTables.keys
}

private const val TABLE_SF6_CHARACTERS = "SF6_Characters"
private const val TABLE_SF6_MOVE_LIST = "SF6_FrameData"
