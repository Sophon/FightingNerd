package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DragDownSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DreamCancelSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.DustLoopSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.MizuumiSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.SuperComboSqlDelightGameProperties
import io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties.WavuSqlDelightGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties

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
            WikiClientFeature.Wavu -> wavuGameProperties
            WikiClientFeature.Mizuumi -> mizuumiGameProperties
            WikiClientFeature.DustLoop -> dustLoopGameProperties
            WikiClientFeature.SuperCombo -> superComboGameProperties
            WikiClientFeature.DragDown -> dragDownGameProperties
            WikiClientFeature.DreamCancel -> dreamCancelGameProperties
            WikiClientFeature.Xko -> NoGameProperties
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

    override fun loadMoveProperties(
        game: Game,
        characterId: CharacterId,
    ): Map<Long, MoveGameProperties> = emptyMap()
}
