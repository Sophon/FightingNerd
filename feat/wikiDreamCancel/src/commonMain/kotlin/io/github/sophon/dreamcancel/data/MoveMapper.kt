package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.dreamcancel.FEATURE_URL

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    return cargoQuery
        .groupBy { it.title.chara }
        .map { (charName, moveDtoList) ->
            val character = charName.toDomain(gameId)
            val moveList = moveDtoList.map {
                it.title.toDomain(gameId, character, imageUrlMap)
            }

            character to moveList
        }
        .toMap()
}

internal fun MoveDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val move = Move(
        charName = character.displayName,
        id = moveId,
        input = input
            .orDash()
            .decodeHtmlEntities()
            .useForwardVariantOnly()
            .lowercase(),
        damage = damage,
        startup = startup,
        onBlock = blockAdv,
        onHit = hitAdv,
        name = name,
        recovery = recovery,
        active = active,
        urls = Move.Urls(
            characterWiki = character.wikiUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(gameId, chara, name),
        ),
    )
    return move
}

internal fun String.useForwardVariantOnly(): String {
    val parts = split("/")
    if (parts.size < 2) return this

    val middle = parts[1]

    // If there's a third part, append non-digit suffix
    return if (parts.size > 2) {
        val suffix = parts[2].dropWhile { it.isDigit() }
        middle + suffix
    } else {
        middle
    }
}

//https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet/Data#close_C
internal fun formMoveWikiUrl(gameId: String, charName: String, name: String?): String {
    return "${FEATURE_URL}/$gameId/${charName.createQueryName()}/Data#${name.orEmpty().replace(" ", "_")}"
}
