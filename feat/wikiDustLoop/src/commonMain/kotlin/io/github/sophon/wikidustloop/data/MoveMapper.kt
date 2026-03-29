package io.github.sophon.wikidustloop.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikidustloop.util.toClickable

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title
        val move = dto.toDomain(
            gameId,
            characterData,
            imageUrlMap,
        )
        move
    }
}

internal fun MoveDto.toDomain(
    gameId: String,
    characterData: DownloadMoveListUseCase.CharacterData,
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

    return Move(
        charName = chara.orEmpty().cleanHtml(),
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
        invulnerability = invuln?.cleanHtml(),
        aliases = aliases,

        notes = notes.formNotes(),

        urls = Move.Urls(
            characterImage = characterData.imageUrl,
            hitboxImageList = hitboxes
                .orEmpty()
                .split(";", "\\")
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            moveImageList = images
                .orEmpty()
                .split(";", "\\")
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(gameId, this),
        ),

        ggstProperties = Move.GGSTProperties(
            chara = chara,
            name = name,
            damage = damage,
            type = type,
            riscGain = riscGain,
            riscLoss = riscLoss,
            wallDamage = wallDamage,
            inputTension = inputTension,
            chipRatio = chipRatio,
            otgType = OTGType,
            prorate = prorate,
            cancel = cancel,
            level = level,
        ),
        dbfzProperties = Move.DBFZProperties(
            attribute = attribute,
            smash = smash,
            kiGain = kigain,
            prorate = prorate,
            blockStun = blockstun,
            groundHit = groundHit,
            airHit = airHit,
            type = type,
            level = level,
        ),
        gbvsrProperties = Move.GBVSRProperties(
            meter = meter,
            level = level,
            cooldown = cooldown,
            cls = cls,
            type = type,
        ),
        bbProperties = Move.BBProperties(
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
    )
}

internal fun String?.formMoveId(charName: String?): String {
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

internal fun String?.formNotes(): List<String> {
    return this
        ?.cleanHtml()
        ?.split(";")
        ?.mapNotNull { it.trim().toClickable() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()
}

internal fun formMoveWikiUrl(gameId: String, dto: MoveDto): String {
    return "${dto.chara.formWikiUrl(gameId)}#${dto.name?.replace(" ", "_")}"
}

internal fun formAliases(
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

internal fun String?.formNagoriyukiAliases(): List<String> {
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
    return buildList {
        addAll(create2dAliases(isPartial = true))
        addAll(createNarmayaStanceAliases())
    }
}

private fun String.createNarmayaStanceAliases(): List<String> {
    val regex = """^(.+)\[([^]]+)]$""".toRegex()
    val match = regex.find(this) ?: return emptyList()
    val (base, suffix) = match.destructured

    return listOf("${suffix.lowercase()}.${base.lowercase()}")
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
