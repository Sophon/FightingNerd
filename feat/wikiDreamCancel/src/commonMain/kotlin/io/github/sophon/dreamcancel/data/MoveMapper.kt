package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.dreamcancel.domain.FEATURE_URL

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    val grouped = cargoQuery.groupBy { it.title.chara.toDomain(gameId).id }

    val result = grouped.map { (_, moveDtoList) ->
        val character = moveDtoList.first().title.chara.toDomain(gameId)
        val moveList = moveDtoList.map {
            it.title.toDomain(gameId, character, imageUrlMap)
        }

        character to moveList
    }.toMap()

    return result
}

internal fun MoveDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val normalizedInput = this.input
        .orDash()
        .decodeHtmlEntities()
        .normalize2dInputs()
        .lowercase()
    val aliasList = normalizedInput.create2dAliases(isPartial = true)

    val move = Move(
        characterId = character.id,
        id = moveId,
        input = normalizedInput,
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = blockAdv?.cleanHtml(),
        onHit = hitAdv?.cleanHtml(),
        name = name?.cleanHtml(),
        recovery = recovery?.cleanHtml(),
        active = active?.cleanHtml(),
        urls = Move.Urls(
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(gameId, chara),
        ),
        aliases = aliasList,
    )
    return move
}

internal fun formMoveWikiUrl(gameId: String, charName: String): String {
    return "${FEATURE_URL}/$gameId/${charName.createQueryName()}"
}
