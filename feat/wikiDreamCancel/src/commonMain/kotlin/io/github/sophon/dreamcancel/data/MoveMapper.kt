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


