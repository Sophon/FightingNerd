package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.wiki.adapter.outbound.ktor.findMoveIdPrefix
import io.github.sophon.wiki.adapter.outbound.ktor.removeMoveIdPrefix
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.game.MBTLMoveProperties
import io.github.sophon.wiki.model.game.Uni2MoveProperties
import io.github.sophon.wiki.model.game.VSAVMoveProperties
import io.github.sophon.wiki.model.wiki.Game

/**
 * Bulk - the whole move table, grouped into characters by `chara`.
 */
internal fun MizuumiMoveListResponseDto.toDomainAll(
    game: Game,
    iconUrlMap: Map<String, String>,
    hitboxUrlMap: Map<String, String>,
): List<Pair<Character, List<Move>>> {
    val characterWithMovesList = cargoquery
        .groupBy { moveTitle -> moveTitle.title.chara }
        .filter { (_, moveTitleList) -> moveTitleList.size >= MIN_MOVES_PER_CHARACTER }
        .map { (chara, moveTitleList) ->
            val character = chara.toDomain(game, iconUrlMap)
            val moveList = moveTitleList
                .map { moveTitle -> moveTitle.title }
                .toDomain(game, character, hitboxUrlMap)
            character to moveList
        }
    return characterWithMovesList
}

/**
 * Separate - one character's move list.
 */
internal fun MizuumiMoveListResponseDto.toDomain(
    game: Game,
    character: Character,
    hitboxUrlMap: Map<String, String>,
): List<Move> {
    val moveList = cargoquery
        .map { moveTitle -> moveTitle.title }
        .toDomain(game, character, hitboxUrlMap)
    return moveList
}

/**
 * One character's moves - the move ID prefix is that character's.
 */
private fun List<MoveDto>.toDomain(
    game: Game,
    character: Character,
    hitboxUrlMap: Map<String, String>,
): List<Move> {
    val moveIdPrefix = map { dto -> dto.moveId }.findMoveIdPrefix()
    val moveList = map { dto -> dto.toDomain(game, character, moveIdPrefix, hitboxUrlMap) }
    return moveList
}

/**
 * Only cleaning - input normalization and aliases are done by the service.
 * The input comes from the move ID - the wiki's `input` doesn't tell versions apart (`ak_5b_close` / `ak_5b_far`
 * are both `5B`), so it's kept as an alias.
 */
private fun MoveDto.toDomain(
    game: Game,
    character: Character,
    moveIdPrefix: String,
    hitboxUrlMap: Map<String, String>,
): Move {
    val move = Move(
        input = moveId.removeMoveIdPrefix(moveIdPrefix),
        remoteId = moveId,
        aliases = listOfNotNull(input?.decodeHtmlEntities()?.takeIf { it.isNotBlank() }),
        name = name?.cleanHtmlOrNull(),
        damage = (damage?.cleanHtmlOrNull() ?: totaldmg),
        startup = startup?.cleanHtmlOrNull(),
        onHit = (advHit?.cleanHtmlOrNull() ?: onHit?.cleanHtmlOrNull()),
        onBlock = (advBlock?.cleanHtmlOrNull() ?: frameAdv?.cleanHtmlOrNull()),
        recovery = recovery?.cleanHtmlOrNull(),
        active = active?.cleanHtmlOrNull(),
        cancel = cancel?.cleanHtmlOrNull()?.formatCancel(),
        guard = guard?.cleanHtmlOrNull(),
        invulnerability = invul?.cleanHtmlOrNull()?.formPropertiesUrl(),
        type = type?.cleanHtmlOrNull(),
        urls = Move.Urls(
            wikiUrl = character.wikiUrl,
            hitboxImageList = hitboxes.toImageUrlList(hitboxUrlMap),
            moveImageList = images.toImageUrlList(hitboxUrlMap),
        ),
        gameProperties = toGameProperties(game),
    )
    return move
}

private fun MoveDto.toGameProperties(game: Game): MoveGameProperties? {
    val properties = when (game) {
        Game.MBTL -> toMbtlProperties()
        Game.Uni2 -> toUni2Properties()
        Game.VSAV -> toVsavProperties()
        else -> null
    }
    return properties
}

private fun MoveDto.toMbtlProperties(): MBTLMoveProperties = MBTLMoveProperties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    minDamage = minDamage?.cleanHtmlOrNull(),
    mizuumiProperty = property?.cleanHtmlOrNull()?.formPropertiesUrl(),
    cost = cost?.cleanHtmlOrNull(),
    attribute = attribute?.cleanHtmlOrNull(),
    landing = landing?.cleanHtmlOrNull(),
    overall = overall?.cleanHtmlOrNull(),
)

private fun MoveDto.toVsavProperties(): VSAVMoveProperties = VSAVMoveProperties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    whiteDmg = whitedmg?.cleanHtmlOrNull(),
    renda = renda?.cleanHtmlOrNull(),
    meter = meter?.cleanHtmlOrNull(),
    reaction = reaction?.cleanHtmlOrNull(),
    curseTime = cursetime?.cleanHtmlOrNull(),
)

private fun MoveDto.toUni2Properties(): Uni2MoveProperties = Uni2MoveProperties(
    inputInfo = inputInfo?.cleanHtmlOrNull(),
    subtitle = subtitle?.cleanHtmlOrNull(),
    minDamage = minDamage?.cleanHtmlOrNull(),
    cancelWindow = cancelWindow?.cleanHtmlOrNull(),
    mizuumiProperty = property?.cleanHtmlOrNull()?.formPropertiesUrl(),
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

private fun String?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .split(",")
        .mapNotNull { fileName -> imageUrlMap[fileName.trim()] }
    return imageUrlList
}

private fun String.formPropertiesUrl(): String {
    val wikiLinkPattern = Regex("""\[\[([^|\]]+)\|([^\]]+)\]\]""")

    val formatted = wikiLinkPattern.replace(this) { matchResult ->
        val fullLink = matchResult.groupValues[1]
        val displayText = matchResult.groupValues[2]
        val url = "$WIKI_BASE_URL/${fullLink.replace(" ", "_")}"
        "[$displayText]($url)"
    }
    return formatted
}

private fun String.formatCancel(): String {
    val formatted = replace("-", "")
    return formatted
}


private const val MIN_MOVES_PER_CHARACTER = 10
private const val WIKI_BASE_URL = "https://mizuumi.wiki/w"
