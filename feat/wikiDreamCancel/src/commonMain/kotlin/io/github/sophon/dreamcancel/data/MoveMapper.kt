package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.dreamcancel.FEATURE_URL

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
        input = input.orDash(),
        damage = damage,
        startup = startup,
        onBlock = blockAdv,
        onHit = hitAdv,
        name = name,
        recovery = recovery,
        active = active,
        urls = Move.Urls(
            hitboxImage = hitboxes?.let {
                val first = it.split(", ").first()
                imageUrlMap[first]
            },
            characterWiki = character.wikiUrl,
        ),
    )
}

internal fun String.toDomain(
    gameId: String,
): Character {
    val idName = this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_") { it.lowercase() }
    val displayName = this
        .cleanHtml()
    val queryName = this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")

    val char = Character(
        id = idName,
        displayName = displayName,
        aliasList = displayName.createAliases(),
        queryName = queryName,
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
    )

    return char
}

private fun String.createAliases(): List<String> {
    val words = split(' ')

    return if (words.size >= 2) {
        buildList {
            var initials = ""
            words.forEach { word ->
                takeIf { word.length >= 2 }?.let { add(word.lowercase()) }
                initials += word.first()
            }
            initials.takeIf { it.isNotBlank() }?.let { add(initials) }
        }
    } else {
        emptyList()
    }
}

