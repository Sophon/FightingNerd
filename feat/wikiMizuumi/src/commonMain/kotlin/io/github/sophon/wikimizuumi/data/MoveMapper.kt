package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikimizuumi.WIKI_BASE_URL

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    return cargoquery
        .groupBy { it.title.chara }
        .filter { it.value.size >= 10 }
        .map { (charName, moveDtoList) ->
            val character = charName.toDomain(gameId)
            val moveList = moveDtoList.map {
                it.title.toDomain(character, imageUrlMap)
            }
            character to moveList
        }.toMap()
}

internal fun MoveDto.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val moveName = name?.cleanHtml()

    val move = Move(
        charName = character.displayName,
        id = moveId,
        input = input
            .orDash()
            .decodeHtmlEntities()
            .lowercase(),
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = frameAdv?.cleanHtml(),
        name = moveName,
        recovery = recovery?.cleanHtml(),
        active = active?.cleanHtml(),
        cancel = cancel?.cleanHtml()?.formatCancel(),
        guard = guard?.cleanHtml(),
        invulnerability = invul?.cleanHtml(),
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
        ),
        mbProperties = Move.MBProperties(
            inputInfo = inputInfo?.cleanHtml(),
            subtitle = subtitle?.cleanHtml(),
            minDamage = minDamage?.cleanHtml(),
            property = property?.cleanHtml()?.formPropertiesUrl(),
            cost = cost?.cleanHtml(),
            attribute = attribute?.cleanHtml(),
            landing = landing?.cleanHtml(),
            overall = overall?.cleanHtml(),
        ),
    )
    return move
}

private fun String.formatCancel(): String {
    return this.replace("-", "")
}

internal fun String.formPropertiesUrl(): String {
    if (startsWith("[[").not() || endsWith("]]").not()) return this

    val parts = this
        .substring(2, this.length - 2)
        .split("|")
    val url = "$WIKI_BASE_URL/${parts.first().replace(" ", "_")}"

    return "[${parts.last()}]($url)"
}