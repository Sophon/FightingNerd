package io.github.sophon.xko.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.xko.URL_HITBOX_PREFIX
import io.github.sophon.xko.URL_HITBOX_SUFIX

internal fun MoveListResponseDto.toDomain(): Map<Character, List<Move>> {
    return bucket
        .groupBy { it.pageName.toCharacter() }
        .mapValues { (character, moveList) ->
            moveList.map { it.toMoveList(charWikiUrl = character.wikiUrl) }
        }
}

internal fun MoveDto.toMoveList(
    charWikiUrl: String,
): Move {
    return Move(
        charName = pageName,
        id = "${input.lowercase()}_$input",
        input = input,
        damage = damage?.ifEmpty { null },
        startup = startup,
        onBlock = onBlock?.ifEmpty { null },
        recovery = recovery,
        active = active?.ifEmpty { null },

        urls = Move.Urls(
            hitboxImage = "$URL_HITBOX_PREFIX/${pageName}_${input}_$URL_HITBOX_SUFIX",
            characterWiki = charWikiUrl,
        ),

        xkoProperties = Move.XkoProperties(
            cancel = cancel?.ifEmpty { null },
            guard = guard?.ifEmpty { null },
            invulnerability = invuln?.ifEmpty { null }
        )
    )
}

internal fun String.toCharacter(): Character {
    return Character(
        id = this,
        displayName = this,
        queryName = this,
        wikiUrl = "https://wiki.play2xko.com/en-us/$this"
    )
}