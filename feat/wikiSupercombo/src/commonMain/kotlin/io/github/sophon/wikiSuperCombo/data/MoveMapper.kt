package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikiSuperCombo.domain.WIKI_BASE_URL
import io.github.sophon.wikiSuperCombo.util.cleanMoveInput

internal fun MoveListResponseDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>
): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title
        val move = dto.toDomain(gameId, character, imageUrlMap)
        move
    }
}

internal fun MoveDto.toDomain(
    gameId: String,
    character: Character,
    imageUrlMap: Map<String, String>,
): Move {
    val type = getType()
    val normalizedInput = input
        .cleanMoveInput()
        .normalize2dInputs()
        .replace("360+", "360")
    val aliasList = formAliases(normalizedInput, type)
    val onBlock = (blockAdv ?: onBlock).takeIfNotTemplate()?.cleanHtml()
    val onHit = (hitAdv ?: onHit).takeIfNotTemplate()?.cleanHtml()
    val notes = (notes ?: properties)
        .takeIfNotTemplate()
        ?.cleanHtml()
        .extractNotes()

    val move = Move(
        characterId = character.id,
        id = moveId,
        name = name.ignoreImageNames(),

        input = normalizedInput,
        damage = damage.takeIfNotTemplate()?.cleanHtml(),
        startup = startup.takeIfNotTemplate(),
        onBlock = onBlock,
        onHit = onHit,
        onCH = null,
        recovery = recovery.takeIfNotTemplate()?.cleanHtml(),
        notes = notes,
        active = active.takeIfNotTemplate()?.cleanHtml(),
        guard = guard.takeIfNotTemplate(),
        cancel = cancel.takeIfNotTemplate(),
        invulnerability = invuln
            .takeIfNotTemplate()
            ?.cleanHtml()
            ?.takeIf { it.isBlank().not() },

        aliases = aliasList,

        urls = Move.Urls(
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
        ),
        avlProperties = Move.AVLProperties(
            chiDamage = flowDamage,
            flow = flow,
        )
    )
    return move
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

private fun MoveDto.formAliases(
    normalizedInput: String,
    type: Move.SF6Properties.Type?
): List<String> {
    val motionAlias = when (type) {
        Move.SF6Properties.Type.SUPER -> formSuperLevel(moveId, superGainHit)
        else -> this.input.formMotionInput()
    }?.lowercase()
    val aliases2d = normalizedInput.create2dAliases(isPartial = true)
    val result = buildList {
        motionAlias?.let { add(it) }
        addAll(aliases2d)
    }

    return result
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

private fun String?.ignoreImageNames(): String? {
    if (this == null) return this
    if (this.contains("[[") || this.contains("]]") || this.contains(".png")) return null
    return this
}
