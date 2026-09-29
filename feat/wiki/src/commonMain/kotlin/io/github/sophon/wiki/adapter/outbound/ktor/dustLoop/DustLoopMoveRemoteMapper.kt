package io.github.sophon.wiki.adapter.outbound.ktor.dustLoop

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.toClickable
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.BBMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.DBFZMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GBVSRMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.GGMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MTFSMoveProperties

internal fun DustLoopMoveListResponseDto.toDomain(
    game: Game,
    character: Character,
    imageUrlMap: Map<String, String>,
): List<Move> {
    val moveList = cargoQuery.map { query -> query.title.toDomain(game, character, imageUrlMap) }
    return moveList
}

/**
 * Only cleaning - the input and the aliases are normalized by the service.
 */
private fun MoveDto.toDomain(
    game: Game,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val cleanedInput = input.orDash()

    val move = Move(
        name = name?.cleanHtml(),
        input = cleanedInput,
        damage = damage?.cleanHtml(),
        startup = startup?.cleanHtml(),
        onBlock = onBlock?.cleanHtml(),
        onHit = onHit?.cleanHtml(),
        onCH = counter?.cleanHtml(),
        active = active?.cleanHtml(),
        cancel = cancel?.cleanHtml(),
        recovery = recovery?.cleanHtml(),
        guard = guard?.cleanHtml(),
        invulnerability = invuln?.cleanHtml()?.ifBlank { null },
        type = type?.cleanHtml(),
        notes = notes.formNotes(),
        urls = Move.Urls(
            hitboxImageList = hitboxes.toImageUrlList(imageUrlMap),
            moveImageList = images.toImageUrlList(imageUrlMap),
            wikiUrl = formMoveWikiUrl(character),
        ),
        gameProperties = toGameProperties(game),
    )
    return move
}

@Suppress("LongMethod")
private fun MoveDto.toGameProperties(game: Game): MoveGameProperties? {
    val properties = when (game) {
        Game.GGST -> GGMoveProperties(
            riscGain = riscGain,
            riscLoss = riscLoss,
            wallDamage = wallDamage,
            inputTension = inputTension,
            chipRatio = chipRatio,
            otgType = OTGType,
            prorate = prorate,
            level = level,
        )

        Game.BBCF -> BBMoveProperties(
            onODR = onODR,
            attribute = attribute,
            p1 = p1,
            p2 = p2,
            starter = starter,
            level = level,
            blockstun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            groundCH = groundCH,
            airCH = airCH,
            blockstop = blockstop,
            hitstop = hitstop,
            chStop = CHstop,
            cancelTiming = cancelTiming,
        )

        Game.MTFS -> MTFSMoveProperties(
            simpleInput = simpleInput?.cleanHtml(),
            level = level?.cleanHtml(),
            prorate = prorate?.cleanHtml(),
            meterGain = meterGain?.cleanHtml(),
            untechAmount = untechAmount?.cleanHtml(),
            hitboxCaption = hitboxCaption?.cleanHtml(),
        )

        Game.GBVSR -> GBVSRMoveProperties(
            meter = meter,
            level = level,
            cooldown = cooldown,
            cls = cls,
        )

        Game.DBFZ -> DBFZMoveProperties(
            attribute = attribute,
            smash = smash,
            kiGain = kigain,
            prorate = prorate,
            blockStun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            level = level,
        )

        else -> null
    }
    return properties
}

private fun String?.formNotes(): List<String> {
    val noteList = this
        ?.cleanHtml()
        ?.split(";")
        ?.mapNotNull { it.trim().toClickable(WIKI_BASE_URL) }
        ?.filter { it.isNotBlank() }
        .orEmpty()
    return noteList
}

private fun String?.toImageUrlList(imageUrlMap: Map<String, String>): List<String> {
    val imageUrlList = this
        .orEmpty()
        .split(";", "\\")
        .mapNotNull { fileName -> imageUrlMap[fileName.trim()] }
    return imageUrlList
}

/**
 * The wiki anchors a move by its name, or by its input when it has none.
 */
private fun MoveDto.formMoveWikiUrl(character: Character): String {
    val anchor = if (name.isNullOrBlank()) {
        input?.replace(" ", "_")
    } else {
        name.replace(" ", "_")
    }
    val url = "${character.wikiUrl}#$anchor"
    return url
}
