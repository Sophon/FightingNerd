package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.urlDecode
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move

internal fun MoveListResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
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
    return Move(
        charName = character.displayName,
        id = moveId,
        input = input
            .orDash()
            .urlDecode()
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
            hitboxImage = hitboxes?.let {
                val files = it.split(", ")
                files.getOrNull(files.size / 2)?.let { key ->
                    imageUrlMap[key]
                }
            }
        ),
    )
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
