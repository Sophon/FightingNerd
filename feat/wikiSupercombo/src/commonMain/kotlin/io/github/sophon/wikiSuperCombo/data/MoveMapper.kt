package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiSuperCombo.WIKI_BASE_URL
import io.github.sophon.wikiSuperCombo.util.cleanMoveInput

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>
): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title
        val move = dto.toDomain(gameId, characterData, imageUrlMap)
        move
    }
}

fun MoveDto.toDomain(
    gameId: String,
    characterData: DownloadMoveListUseCase.CharacterData,
    imageUrlMap: Map<String, String>,
): Move {
    val type = getType()

    return Move(
        charName = chara,
        id = moveId,
        name = name,

        input = input.cleanMoveInput().replace("360+", "360"),
        damage = damage.takeIfNotTemplate()?.cleanHtml(),
        startup = startup.takeIfNotTemplate(),
        onBlock = blockAdv.takeIfNotTemplate()?.cleanHtml(),
        onHit = hitAdv.takeIfNotTemplate()?.cleanHtml(),
        onCH = null,
        recovery = recovery.takeIfNotTemplate()?.cleanHtml(),
        notes = notes.takeIfNotTemplate()?.cleanHtml()
            .extractNotes(),
        active = active.takeIfNotTemplate()?.cleanHtml(),
        guard = guard.takeIfNotTemplate(),
        cancel = cancel.takeIfNotTemplate(),
        invulnerability = invuln.takeIfNotTemplate()?.cleanHtml(),

        aliases = formAliases(type),

        urls = Move.Urls(
            characterImage = characterData.imageUrl,
            moveImageList = images
                .orEmpty()
                .split(",")
                .map { it.trim() }
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            hitboxImageList = hitboxes
                .orEmpty()
                .split(",")
                .map { it.trim() }
                .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
            wikiUrl = formMoveWikiUrl(
                gameId = gameId,
                charName = chara,
                input = input,
                name = name,
            )
        ),

        sf6Properties = Move.SF6Properties(
            type = type,
            images = images.takeIfNotTemplate()
                ?.split(",")
                ?.map { it.trim() },
            chip = chip.takeIfNotTemplate(),
            dmgScaling = dmgScaling.takeIfNotTemplate(),
            total = total.takeIfNotTemplate(),
            hitConfirm = hitconfirm.takeIfNotTemplate(),
            punishAdv = punishAdv.takeIfNotTemplate()?.cleanHtml(),
            perfParryAdv = perfParryAdv.takeIfNotTemplate()?.cleanHtml(),
            DRcOH = DRcancelHit.takeIfNotTemplate()?.cleanHtml(),
            DRcOB = DRcancelBlk.takeIfNotTemplate()?.cleanHtml(),
            DROH = afterDRHit.takeIfNotTemplate()?.cleanHtml(),
            DROB = afterDRBlk.takeIfNotTemplate()?.cleanHtml(),
            hitStun = hitstun.takeIfNotTemplate()?.cleanHtml(),
            blockStun = blockstun.takeIfNotTemplate()?.cleanHtml(),
            hitStop = hitstop.takeIfNotTemplate()?.cleanHtml(),
            driveDmgOnBlock = driveDmgBlk.takeIfNotTemplate(),
            driveDmgOnHit = driveDmgHit.takeIfNotTemplate(),
            driveGain = driveGain.takeIfNotTemplate(),
            superGainOnHit = superGainHit.takeIfNotTemplate(),
            superGainOnBlock = superGainBlk.takeIfNotTemplate(),
            armor = armor.takeIfNotTemplate(),
            jugStart = jugStart.takeIfNotTemplate()?.cleanHtml(),
            jugIncrease = jugIncrease.takeIfNotTemplate()?.cleanHtml(),
            jugLimit = jugLimit.takeIfNotTemplate(),
            projectileSpeed = projSpeed.takeIfNotTemplate(),
            attackRange = atkRange.takeIfNotTemplate(),
        ),
        mkProperties = Move.MKProperties(
            moveType = moveType.takeIfNotTemplate(),
            cost = moveType
                .split(",")
                .filterNot { it.takeIfNotTemplate() == null }
        )
    )
}

private fun String?.takeIfNotTemplate(): String? {
    return this?.takeUnless { it.matches(Regex("\\{\\{\\{.+\\}\\}\\}")) || it == "-" }
}

private fun String?.extractNotes(): List<String> {
    return this
        ?.split(";")
        ?.map { it.trim() }
        ?: emptyList()
}

private fun MoveDto.getType(): Move.SF6Properties.Type? {
    return when (moveType.lowercase()) {
        "ground_normal" -> Move.SF6Properties.Type.GROUND_NORMAL
        "air_normal" -> Move.SF6Properties.Type.AIR_NORMAL
        "special" -> Move.SF6Properties.Type.SPECIAL
        "super" -> Move.SF6Properties.Type.SUPER
        "throw" -> Move.SF6Properties.Type.THROW
        "drive" -> Move.SF6Properties.Type.DRIVE
        "taunt" -> Move.SF6Properties.Type.TAUNT
        else -> null
    }
}

internal fun formMoveWikiUrl(
    gameId: String,
    charName: String,
    input: String,
    name: String?,
): String {
    if (input.contains("direction", ignoreCase = true)) return ""

    val input = input
        .replace(" ", "_")
    val moveId = if (name.isNullOrBlank().not()) {
        "${name.replace(" ", "_")}_($input)"
    } else {
        input
    }
    val charQueryName = charName.replace(" ", "_")

    val url = when (gameId) {
        Game.StreetFighter6.id -> {
            "${WIKI_BASE_URL}/$gameId/$charQueryName#$moveId"
        }
        Game.MK1.id -> {
            "${WIKI_BASE_URL}/$gameId/$charQueryName/Data#$input"
        }
        else -> WIKI_BASE_URL
    }

    return url
}

private fun MoveDto.formAliases(type: Move.SF6Properties.Type?): List<String> {
    val motionAlias = when (type) {
        Move.SF6Properties.Type.SUPER -> formSuperLevel(moveId, superGainHit)
        else -> this.input.formMotionInput()
    }?.lowercase()

    val orAliases = if (input.contains("/")) {
        val directions = input
            .substringBefore(input.first { it.isLetter() })
            .split("/")
        val button = input.dropWhile { it.isLetter().not() }
        directions.map { "$it$button".lowercase() }
    } else {
        emptyList()
    }

    return listOfNotNull(motionAlias) + orAliases
}

private fun formSuperLevel(
    moveId: String,
    superGain: String?,
): String? {
    if (moveId.contains("(ca)", ignoreCase = true)) return null
    val superCost = superGain?.toIntOrNull() ?: return null

    return when (superCost) {
        -10_000 -> "sa1"
        -20_000 -> "sa2"
        else -> "sa3"
    }
}

private fun String?.formMotionInput(): String? {
    val motion = when {
        this == null -> return null
        this.startsWith("41236") -> this.replaceFirst("41236", "hcf")
        this.startsWith("63214") -> this.replaceFirst("63214", "hcb")
        this.startsWith("214") -> this.replace("214", "qcb")
        this.startsWith("236") -> this.replace("236", "qcf")
        this.startsWith("623") -> this.replaceFirst("623", "dp")
        this.startsWith("421") -> this.replaceFirst("421", "bdp")
        this.startsWith("360+") -> this.replaceFirst("360+", "spd")
        this.startsWith("360") -> this.replaceFirst("360", "spd")
        this.startsWith("2") -> this.replaceFirst("2", "cr")
        this.startsWith("5") -> this.replaceFirst("5", "st")
        else -> return null
    }

    return motion
}
