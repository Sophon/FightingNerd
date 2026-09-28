package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.COTWMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.KOF15MoveProperties

/**
 * Bulk - the whole move table, grouped into characters. `chara` spellings that form the same id are one character.
 */
internal fun DreamCancelMoveListResponseDto.toDomainAll(
    game: Game,
    iconUrlMap: Map<String, String>,
    hitboxUrlMap: Map<String, String>,
): List<Pair<Character, List<Move>>> {
    val characterWithMovesList = cargoQuery
        .groupBy { query -> query.title.chara.toCharacter(game, iconUrlMap).id }
        .map { (_, queryList) ->
            val character = queryList.first().title.chara.toCharacter(game, iconUrlMap)
            val moveList = queryList.map { query -> query.title.toDomain(game, character, hitboxUrlMap) }
            character to moveList
        }
    return characterWithMovesList
}

/**
 * Only cleaning - input normalization and aliases are done by the service.
 */
private fun MoveDto.toDomain(
    game: Game,
    character: Character,
    hitboxUrlMap: Map<String, String>,
): Move {
    val cleanedInput = input
        .orDash()
        .decodeHtmlEntities()

    val move = Move(
        characterId = character.id,
        id = moveId,
        input = cleanedInput,
        name = name?.cleanHtml(),
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = blockAdv?.cleanHtml(),
        onHit = hitAdv?.cleanHtml(),
        recovery = recovery?.cleanHtml(),
        active = active?.cleanHtml(),
        urls = Move.Urls(
            hitboxImageList = hitboxes.toImageUrlList(hitboxUrlMap),
            moveImageList = images.toImageUrlList(hitboxUrlMap),
            wikiUrl = "$WIKI_BASE_URL/${game.id}/${chara.createQueryName()}",
        ),
        gameProperties = toGameProperties(game),
    )
    return move
}

private fun MoveDto.toGameProperties(game: Game): MoveGameProperties? {
    val properties = when (game) {
        Game.KoFXV -> KOF15MoveProperties(stun = stun?.cleanHtml())
        Game.COTW -> COTWMoveProperties(revDamage = revDamage?.cleanHtml())
        else -> null
    }
    return properties
}

private fun String?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .split(",")
        .mapNotNull { fileName -> imageUrlMap[fileName.trim()] }
    return imageUrlList
}
