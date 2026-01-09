package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikimizuumi.WIKI_BASE_URL

internal fun MoveListResponseDto.toDomainAll(
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

internal fun MoveListResponseDto.toDomain(
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
): List<Move> {
    return cargoquery
        .map {
            val dto = it.title
            dto.toDomain(characterData, imageUrlMap)
        }
}

internal fun MoveDto.toDomain(
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val moveName = name?.cleanHtmlOrNull()

    val move = Move(
        charName = character.displayName,
        id = moveId,
        input = input
            .orDash()
            .decodeHtmlEntities()
            .lowercase(),
        damage = damage?.cleanHtmlOrNull(),
        startup = startup?.cleanHtmlOrNull(),
        onHit = onHit?.cleanHtmlOrNull(),
        onBlock = frameAdv?.cleanHtmlOrNull(),
        name = moveName,
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull(),
        urls = Move.Urls(
            wikiUrl = WIKI_BASE_URL,
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
            inputInfo = inputInfo?.cleanHtmlOrNull(),
            subtitle = subtitle?.cleanHtmlOrNull(),
            minDamage = minDamage?.cleanHtmlOrNull(),
            property = property?.cleanHtmlOrNull()?.formPropertiesUrl(),
            cost = cost?.cleanHtmlOrNull(),
            attribute = attribute?.cleanHtmlOrNull(),
            landing = landing?.cleanHtmlOrNull(),
            overall = overall?.cleanHtmlOrNull(),
        ),
        uni2Properties = Move.Uni2Properties(
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
    )
    return move
}

private fun String.formatCancel(): String {
    return this.replace("-", "")
}

internal fun String.formPropertiesUrl(): String {
    val wikiLinkPattern = Regex("""\[\[([^|\]]+)\|([^\]]+)\]\]""")

    val final = wikiLinkPattern.replace(this) { matchResult ->
        val fullLink = matchResult.groupValues[1]
        val displayText = matchResult.groupValues[2]
        val url = "$WIKI_BASE_URL/${fullLink.replace(" ", "_")}"
        "[$displayText]($url)"
    }

    return final
}

internal fun MoveDto.toDomain(
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
): Move {
    val moveName = name?.cleanHtmlOrNull()

    val move = Move(
        charName = this.chara,
        id = moveId,
        input = input
            .orDash()
            .decodeHtmlEntities()
            .lowercase(),
        damage = damage?.cleanHtmlOrNull(),
        startup = startup?.cleanHtmlOrNull(),
        onHit = onHit?.cleanHtmlOrNull(),
        onBlock = frameAdv?.cleanHtmlOrNull(),
        name = moveName,
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull(),
        urls = Move.Urls(
            wikiUrl = WIKI_BASE_URL,
            characterImage = characterData.imageUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(",")
                .mapNotNull { imageUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
        ),
        uni2Properties = Move.Uni2Properties(
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
    )
    return move
}