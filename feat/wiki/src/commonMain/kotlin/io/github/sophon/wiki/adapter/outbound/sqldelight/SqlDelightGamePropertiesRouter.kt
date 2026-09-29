package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.wiki.adapter.outbound.sqldelight.wavu.WavuSqlDelightGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties

/**
 * Routes each game to its wiki's extension tables - a wiki without them doesn't compile.
 */
internal class SqlDelightGamePropertiesRouter(
    private val wavuGameProperties: WavuSqlDelightGameProperties,
) {
    fun of(game: Game): SqlDelightGameProperties {
        val gameProperties = when (game.wiki) {
            WikiClientFeature.Wavu -> wavuGameProperties
            WikiClientFeature.Xko -> NoGameProperties
            // TODO extension tables - after the Tekken 8 review
            WikiClientFeature.Mizuumi,
            WikiClientFeature.DustLoop,
            WikiClientFeature.SuperCombo,
            WikiClientFeature.DragDown,
            WikiClientFeature.DreamCancel -> NoGameProperties
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
