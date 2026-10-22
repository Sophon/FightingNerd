package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DragDownSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DreamCancelSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DustLoopSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.MizuumiSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SuperComboSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.WavuSqlDelightGameProperties
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.model.wiki.Wiki

/**
 * Routes each game to its wiki's extension tables - a wiki without them doesn't compile.
 */
internal class SqlDelightGamePropertiesRouter(
    private val wavuGameProperties: WavuSqlDelightGameProperties,
    private val mizuumiGameProperties: MizuumiSqlDelightGameProperties,
    private val dustLoopGameProperties: DustLoopSqlDelightGameProperties,
    private val superComboGameProperties: SuperComboSqlDelightGameProperties,
    private val dragDownGameProperties: DragDownSqlDelightGameProperties,
    private val dreamCancelGameProperties: DreamCancelSqlDelightGameProperties,
) {
    fun of(game: Game): SqlDelightGameProperties {
        val gameProperties = when (game.wiki) {
            Wiki.Wavu -> wavuGameProperties
            Wiki.Mizuumi -> mizuumiGameProperties
            Wiki.DustLoop -> dustLoopGameProperties
            Wiki.SuperCombo -> superComboGameProperties
            Wiki.DragDown -> dragDownGameProperties
            Wiki.DreamCancel -> dreamCancelGameProperties
            Wiki.Xko -> NoGameProperties
        }
        return gameProperties
    }
}

/**
 * 2XKO has no game properties.
 */
private object NoGameProperties : SqlDelightGameProperties {
    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) = Unit

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) = Unit

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> = emptyMap()

    override fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties> = emptyMap()
}
