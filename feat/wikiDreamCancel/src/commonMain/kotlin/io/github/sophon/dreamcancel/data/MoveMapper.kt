package io.github.sophon.dreamcancel.data

import io.github.sophon.core.util.add2dAliases
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.useForwardVariantOnly
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
    val normalizedInput = this.input
        .orDash()
        .decodeHtmlEntities()
        .normalize2dInputs(minimizeClose = false)
        .useForwardVariantOnly()
        .lowercase()
    val aliasList = normalizedInput.add2dAliases() + normalizedInput.addAliasesForMultipleButtons()

    val move = Move(
        charName = character.displayName,
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
            characterWiki = character.wikiUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(gameId, chara, name),
        ),
        aliases = aliasList,
    )
    return move
}

internal fun formMoveWikiUrl(gameId: String, charName: String, name: String?): String {
    return "${FEATURE_URL}/$gameId/${charName.createQueryName()}/Data#${name.orEmpty().replace(" ", "_")}"
}

private fun String.addAliasesForMultipleButtons(): List<String> {
    if (contains("/").not()) return listOf(this)

    val base = substringBefore("/")
    val buttons = substringAfter("/").map { it.toString() }

    return listOf(base) + buttons.map { base.dropLast(1) + it }
}
