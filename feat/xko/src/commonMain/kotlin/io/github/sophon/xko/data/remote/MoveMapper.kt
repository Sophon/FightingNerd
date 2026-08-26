package io.github.sophon.xko.data.remote

import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.xko.domain.FEATURE_URL
import io.github.sophon.xko.domain.URL_HITBOX_PREFIX
import io.github.sophon.xko.domain.URL_HITBOX_SUFIX

internal fun MoveListResponseDto.toDomain(): Map<Character, List<Move>> {
    return bucket
        .groupBy { it.pageName.toCharacter() }
        .filterOutTemplates()
        .mapValues { (_, moveList) ->
            moveList.map { it.toMoveList() }
        }
}

private fun MoveDto.toMoveList(
): Move {
    val characterId = pageName
        .replace(" ", "_")
        .lowercase()
    val formattedInput = input.orDash().lowercase()
    val aliases = formattedInput
        .create2dAliases(isPartial = false)
        .addExtraAliases(formattedInput)

    val move = Move(
        characterId = characterId,
        id = "${characterId}_$formattedInput",

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
            wikiUrl = "$FEATURE_URL/${pageName}#${input}"
        ),
    )

    return move
}

private fun Map<Character, List<MoveDto>>.filterOutTemplates(): Map<Character, List<MoveDto>> {
    val filtered = filter { it.key.id.contains(":").not() && it.key.id.contains(" poc").not() }
    return filtered
}

internal fun List<String>.addExtraAliases(formattedInput: String): List<String> {
    return buildList {
        addAll(this@addExtraAliases)

        if ("~" in formattedInput) {
            add(formattedInput.replace("~", ""))
        }

        if (formattedInput.contains("(") && "+" in formattedInput && formattedInput.endsWith(")")) {
            add(formattedInput.replace("(", "").dropLast(1))
        }
    }
}