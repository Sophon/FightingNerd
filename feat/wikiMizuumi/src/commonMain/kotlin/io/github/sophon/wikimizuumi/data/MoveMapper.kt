package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikimizuumi.domain.WIKI_BASE_URL

internal fun MoveListResponseDto.toDomainAll(
    gameId: String,
    imageUrlMap: Map<String, String>,
    hitboxUrlMap: Map<String, String>,
): Map<Character, List<Move>> {
    return cargoquery
        .groupBy { it.title.chara }
        .filter { it.value.size >= 10 }
        .map { (charName, moveDtoList) ->
            val character = charName.toDomain(gameId, imageUrlMap)
            val moveList = moveDtoList.map {
                it.title.toDomain(character, hitboxUrlMap)
            }
            character to moveList
        }.toMap()
}

//MBVS
internal fun MoveDto.toDomain(
    character: Character,
    hitboxUrlMap: Map<String, String>,
): Move {
    val moveName = name?.cleanHtmlOrNull()
    val normalizedInput = this.input
        .orDash()
        .decodeHtmlEntities()
        .normalize2dInputs()
    val aliases = normalizedInput.create2dAliases(isPartial = true)

    val move = Move(
        charName = character.displayName,
        id = moveId,
        input = normalizedInput,
        damage = damage?.cleanHtmlOrNull() ?: totaldmg,
        startup = startup?.cleanHtmlOrNull(),
        onHit = onHit?.cleanHtmlOrNull() ?: advHit?.cleanHtmlOrNull(),
        onBlock = frameAdv?.cleanHtmlOrNull() ?: advBlock?.cleanHtmlOrNull(),
        name = moveName,
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull()?.formPropertiesUrl()?.formPropertiesUrl(),
        urls = Move.Urls(
            characterImage = character.images?.iconUrl,
            wikiUrl = character.wikiUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .mapNotNull { hitboxUrlMap.getOrElse(key = it.trim(), defaultValue = { null }) },
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
        vsavProperties = Move.VSAVProperties(
            inputInfo = inputInfo?.cleanHtmlOrNull(),
            subtitle = subtitle?.cleanHtmlOrNull(),
            whiteDmg = whitedmg?.cleanHtmlOrNull(),
            renda = renda?.cleanHtmlOrNull(),
            meter = meter?.cleanHtmlOrNull(),
            reaction = reaction?.cleanHtmlOrNull(),
            curseTime = cursetime?.cleanHtmlOrNull(),
        ),
        aliases = aliases,
    )
    return move
}

internal fun MoveListResponseDto.toDomain(
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
    gameId: String,
): List<Move> {
    return cargoquery
        .map {
            val dto = it.title
            dto.toDomain(characterData, imageUrlMap, gameId)
        }
}

//Uni
internal fun MoveDto.toDomain(
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
    gameId: String,
): Move {
    val game = Game.fromId(gameId)
    val moveName = name?.cleanHtmlOrNull()
    val normalizedInput = this.input
        .orDash()
        .decodeHtmlEntities()
        .normalize2dInputs()
    val aliasList = normalizedInput.create2dAliases(isPartial = true)

    val move = Move(
        charName = this.chara,
        id = moveId,
        input = normalizedInput,
        damage = damage?.cleanHtmlOrNull(),
        startup = startup?.cleanHtmlOrNull(),
        onHit = onHit?.cleanHtmlOrNull(),
        onBlock = frameAdv?.cleanHtmlOrNull(),
        name = moveName,
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull()?.formPropertiesUrl(),
        urls = Move.Urls(
            wikiUrl = game?.wikiUrl ?: WIKI_BASE_URL,
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
        ),
        aliases = aliasList,
    )
    return move
}

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