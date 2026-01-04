package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikimizuumi.FEATURE_URL

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    return cargoquery
        .groupBy { it.title.chara }
        .map { (charName, moveDtoList) ->
            val character = charName.toDomain(gameId)
            val moveList = moveDtoList.map {
                it.title.toDomain(gameId, character, imageUrlMap)
            }
            character to moveList
        }.toMap()
}

internal fun MoveDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val moveName = name?.cleanHtml()

    val move = Move(
        charName = character.displayName,
        id = moveId,
        input = input
            .orDash()
            .decodeHtmlEntities()
            .lowercase(),
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = frameAdv?.cleanHtml(),
        name = moveName,
        recovery = recovery?.cleanHtml(),
        active = active?.cleanHtml(),
        urls = Move.Urls(
            characterWiki = character.wikiUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(gameId, chara, moveName),
        ),
    )
    return move
}

private fun formMoveWikiUrl(gameId: String, chara: String, moveName: String?): String {
    val subDomain = when (gameId) {
        Game.MBTL.id -> "Melty_Blood"
        else -> ""
    }
    val charQueryName = chara.replace(' ', '_')
    val moveQueryName = moveName.orEmpty().replace(' ', '_')

    return "${FEATURE_URL}/$subDomain/$gameId/$charQueryName#$moveQueryName"
}