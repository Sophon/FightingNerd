package io.github.sophon.wikidustloop.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.toClickable
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidustloop.domain.WIKI_BASE_URL
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGSTMoveProperties
import io.github.sophon.wikidustloop.integration.model.MTFSMoveProperties

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title
        val move = dto.toDomain(
            gameId,
            character,
            imageUrlMap,
        )
        move
    }
}

internal fun MoveDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val normalizedInput = input
        .orDash()
        .normalize2dInputs()
    val aliases = formAliases(
        gameId = gameId,
        input = normalizedInput,
        charName = chara,
    )

    val gameProperties = when (Game.fromId(gameId)) {
        Game.GGST -> {
            GGSTMoveProperties(
                type = type,
                riscGain = riscGain,
                riscLoss = riscLoss,
                wallDamage = wallDamage,
                inputTension = inputTension,
                chipRatio = chipRatio,
                otgType = OTGType,
                prorate = prorate,
                level = level,
            )
        }
        Game.BBCF -> {
            BBMoveProperties(
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
                type = type,
            )
        }
        Game.MTFS -> {
            MTFSMoveProperties(
                simpleInput = simpleInput?.cleanHtml(),
                type = type?.cleanHtml(),
                level = level?.cleanHtml(),
                prorate = prorate?.cleanHtml(),
                meterGain = meterGain?.cleanHtml(),
                untechAmount = untechAmount?.cleanHtml(),
                hitboxCaption = hitboxCaption?.cleanHtml(),
            )
        }
        Game.GBVSR -> {
            GBVSRMoveProperties(
                meter = meter,
                level = level,
                cooldown = cooldown,
                cls = cls,
                type = type,
            )
        }
        Game.DBFZ -> {
            DBFZMoveProperties(
                attribute = attribute,
                smash = smash,
                kiGain = kigain,
                prorate = prorate,
                blockStun = blockstun,
                groundHit = groundHit,
                airHit = airHit,
                type = type,
                level = level,
            )
        }
        else -> null
    }

    val move = Move(
        characterId = character.id,
        id = normalizedInput.formMoveId(chara),
        name = name?.cleanHtml(),

        input = normalizedInput,
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
        aliases = aliases,

        notes = notes.formNotes(),

        urls = Move.Urls(
            hitboxImageList = hitboxes
                .orEmpty()
                .split(";", "\\")
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(";", "\\")
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(this, character),
        ),

        gameProperties = gameProperties,
    )

    return move
}

private fun String?.formMoveId(charName: String?): String {
    val charNameId = charName.orEmpty()
        .replace("'", "")
        .replace(".", "")
        .replace("?", "")
        .split(" ")
        .joinToString("_") { it.lowercase() }
    val moveId = this
        .orEmpty()
        .lowercase()
        .replace(" ", "")

    return "${charNameId}_$moveId"
}

private fun String?.formNotes(): List<String> {
    return this
        ?.cleanHtml()
        ?.split(";")
        ?.mapNotNull { it.trim().toClickable(WIKI_BASE_URL) }
        ?.filter { it.isNotBlank() }
        ?: emptyList()
}

private fun formMoveWikiUrl(dto: MoveDto, character: Character): String {
    val moveId = if (dto.name.isNullOrBlank()) {
        dto.input?.replace(" ", "_")
    } else {
        dto.name.replace(" ", "_")
    }
    val url = "${character.wikiUrl}#${moveId}"
    return url
}

private fun formAliases(
    gameId: String,
    input: String?,
    charName: String?,
): List<String> {
    if (input == null) return emptyList()

    val game = Game.fromId(gameId)
    val aliases = when {
        charName == "Nagoriyuki" -> input.formNagoriyukiAliases()
        game == Game.GBVSR -> input.createGbvsAliases()
        else -> { input.create2dAliases(isPartial = false) }
    }
        .addAliasForReleaseNotation(input)
        .distinct()

    return aliases
}

private fun String?.formNagoriyukiAliases(): List<String> {
    val result = when {
        this == null -> emptyList()
        contains("levelbr", ignoreCase = true) -> listOf(replace("levelbr", "b", ignoreCase = true).lowercase())
        contains("level1", ignoreCase = true) -> listOf(replace("level1", "", ignoreCase = true).lowercase())
        contains("level", ignoreCase = true) -> listOf(replace("level", "", ignoreCase = true).lowercase())
        else -> emptyList()
    }
    return result
}

private fun String.createGbvsAliases(): List<String> {
    val result = buildList {
        addAll(create2dAliases(isPartial = true))
        addAll(createNarmayaStanceAliases())
    }
    return result
}

private fun String.createNarmayaStanceAliases(): List<String> {
    val regex = """^(.+)\[([^]]+)]$""".toRegex()
    val match = regex.find(this) ?: return emptyList()
    val (base, suffix) = match.destructured

    val result = listOf("${suffix.lowercase()}.${base.lowercase()}")
    return result
}

private fun List<String>.addAliasForReleaseNotation(input: String): List<String> {
    val regex = Regex("""\]([a-zA-Z])\[""")

    val result = if (regex.containsMatchIn(input)) {
        val transformed = regex.replace(input, "$1")
        this + transformed
    } else {
        this
    }

    return result
}
