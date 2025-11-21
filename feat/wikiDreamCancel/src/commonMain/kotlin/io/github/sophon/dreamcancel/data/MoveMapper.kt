package io.github.sophon.dreamcancel.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.dreamcancel.URL_KOF_15

internal fun MoveListResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): Map<Character, List<Move>> {
    return cargoQuery
        .groupBy { it.title.chara.toDomain(gameId) }
        .mapValues { (_, moveDtoList) ->
            moveDtoList.map {
                it.title.toDomain(imageUrlMap, gameId)
            }
        }
}

internal fun MoveDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): Move {
    return Move(
        charName = chara.toId(),
        id = moveId,
        input = input,
        damage = damage,
        startup = startup,
        onBlock = blockAdv,
        onHit = hitAdv,
        name = name,
        recovery = recovery,
        active = active,
        hitboxImageUrl = hitboxes?.let {
            val first = it.split(", ").first()
            imageUrlMap[first]
        }
    )
}

internal fun String.toDomain(
    gameId: String,
): Character {
    val queryName = this.toQuery()

    return Character(
        id = this.toId(),
        displayName = this,
        queryName = queryName,
        wikiUrl = "$URL_KOF_15/$gameId/$queryName",
    )
}

internal fun String.toId(): String {
    return this
        .split(' ')
        .joinToString("_") { it.lowercase() }
}

internal fun String.toQuery(): String {
    return this
        .split(' ')
        .joinToString("_")
}

