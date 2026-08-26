package io.github.sophon.wikimizuumi.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.chargeAlias
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikimizuumi.domain.WIKI_BASE_URL

internal fun MoveListResponseDto.toDomainAll(
    game: Game,
    imageUrlMap: Map<String, String>,
    hitboxUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    val map = cargoquery
        .groupBy { it.title.chara }
        .filter { it.value.size >= 10 }
        .map { (charName, moveDtoList) ->
            val character = charName.toDomain(game.id, imageUrlMap)
            val moveList = moveDtoList.map {
                it.title.toDomain(character, hitboxUrlMap)
            }
            character to moveList
        }.toMap()
    return map
}

internal fun MoveDto.toDomain(
    character: Character,
    hitboxUrlMap: Map<String, String>,
): Move {
    val normalizedInput = input
        .orDash()
        .decodeHtmlEntities()
        .normalize2dInputs()

    val move = Move(
        characterId = character.id,
        id = moveId,
        input = normalizedInput,
        name = name?.cleanHtmlOrNull(),
        damage = damage?.cleanHtmlOrNull() ?: totaldmg,
        startup = startup?.cleanHtmlOrNull(),
        onHit = advHit?.cleanHtmlOrNull() ?: onHit?.cleanHtmlOrNull(),
        onBlock = advBlock?.cleanHtmlOrNull() ?: frameAdv?.cleanHtmlOrNull(),
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull()?.formPropertiesUrl(),
        urls = Move.Urls(
            wikiUrl = character.wikiUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { hitboxUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(",")
                .mapNotNull { hitboxUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
        ),
        mbProperties = toMbProperties(),
        vsavProperties = toVsavProperties(),
        uni2Properties = toUni2Properties(),
        aliases = normalizedInput.create2dAliases(isPartial = true) + normalizedInput.chargeAlias(),
    )
    return move
}

private fun MoveDto.toMbProperties() = Move.MBProperties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    minDamage = minDamage?.cleanHtmlOrNull(),
    property = property?.cleanHtmlOrNull()?.formPropertiesUrl(),
    cost = cost?.cleanHtmlOrNull(),
    attribute = attribute?.cleanHtmlOrNull(),
    landing = landing?.cleanHtmlOrNull(),
    overall = overall?.cleanHtmlOrNull(),
)

private fun MoveDto.toVsavProperties() = Move.VSAVProperties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    whiteDmg = whitedmg?.cleanHtmlOrNull(),
    renda = renda?.cleanHtmlOrNull(),
    meter = meter?.cleanHtmlOrNull(),
    reaction = reaction?.cleanHtmlOrNull(),
    curseTime = cursetime?.cleanHtmlOrNull(),
)

private fun MoveDto.toUni2Properties() = Move.Uni2Properties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    minDamage = minDamage?.cleanHtmlOrNull(),
    type = type?.cleanHtmlOrNull(),
    cancelWindow = cancelWindow?.cleanHtmlOrNull(),
    property = property?.cleanHtmlOrNull()?.formPropertiesUrl(),
    cost = cost?.cleanHtmlOrNull(),
    attribute = attribute?.cleanHtmlOrNull(),
    landing = landing?.cleanHtmlOrNull(),
    overall = overall?.cleanHtmlOrNull(),
    assaultAdv = assaultAdv?.cleanHtmlOrNull(),
    blockstun = blockstun?.cleanHtmlOrNull(),
    groundHit = groundHit?.cleanHtmlOrNull(),
    airHit = airHit?.cleanHtmlOrNull(),
    groundCH = groundCH?.cleanHtmlOrNull(),
    airCH = airCH?.cleanHtmlOrNull(),
    hitstop = hitstop?.cleanHtmlOrNull(),
    CHstop = CHstop?.cleanHtmlOrNull(),
    proration = proration?.cleanHtmlOrNull(),
    comboP1 = comboP1?.cleanHtmlOrNull(),
    comboP2 = comboP2?.cleanHtmlOrNull(),
)

private fun String.formPropertiesUrl(): String {
    val wikiLinkPattern = Regex("""\[\[([^|\]]+)\|([^\]]+)\]\]""")

    val final = wikiLinkPattern.replace(this) { matchResult ->
        val fullLink = matchResult.groupValues[1]
        val displayText = matchResult.groupValues[2]
        val url = "$WIKI_BASE_URL/${fullLink.replace(" ", "_")}"
        "[$displayText]($url)"
    }

    return final
}

private fun String.formatCancel(): String {
    return this.replace("-", "")
}
