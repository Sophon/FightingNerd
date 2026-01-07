package io.github.sophon.xko.data

import io.github.sophon.core.util.createAliasesFromSlash
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.xko.FEATURE_URL
import io.github.sophon.xko.URL_HITBOX_PREFIX
import io.github.sophon.xko.URL_HITBOX_SUFIX

internal fun MoveListResponseDto.toDomain(): Map<Character, List<Move>> {
    return bucket
        .groupBy { it.pageName.toCharacter() }
        .mapValues { (character, moveList) ->
            moveList.map { it.toMoveList(charWikiUrl = character.wikiUrl) }
        }
}

private fun MoveDto.toMoveList(
    charWikiUrl: String,
): Move {
    val charName = pageName
        .replace(" ", "_")
    val formattedInput = input.orDash().lowercase()
    val aliases = formattedInput
        .createAliasesFromSlash()
        .let { aliases ->
            if (formattedInput.contains("~")) {
                aliases + formattedInput.replace("~", "")
            } else {
                aliases
            }
        }

    val move = Move(
        charName = charName,
        id = "${charName.lowercase()}_$formattedInput",

        input = formattedInput,
        damage = damage?.ifEmpty { null },
        startup = startup,
        onBlock = onBlock?.ifEmpty { null },
        recovery = recovery,
        active = active?.ifEmpty { null },
        cancel = cancel?.ifEmpty { null },
        guard = guard?.ifEmpty { null },
        invulnerability = invuln?.ifEmpty { null },
        aliases = aliases,

        urls = Move.Urls(
            hitboxImageList = listOf("$URL_HITBOX_PREFIX/${pageName}_${input}_$URL_HITBOX_SUFIX"),
            moveImageList = listOf("$URL_HITBOX_PREFIX/${pageName}_${input}.png"),
            characterWiki = charWikiUrl,
            wikiUrl = "$FEATURE_URL/${pageName}#${input}"
        ),
    )

    return move
}
