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

        val type = dto.getType()
        val move = Move(
            charName = dto.chara,
            id = dto.moveId,
            name = dto.name,

            input = dto.input.cleanMoveInput(),
            damage = dto.damage.takeIfNotTemplate()?.cleanHtml(),
            startup = dto.startup.takeIfNotTemplate(),
            onBlock = dto.blockAdv.takeIfNotTemplate()?.cleanHtml(),
            onHit = dto.hitAdv.takeIfNotTemplate()?.cleanHtml(),  
            onCH = null,
            recovery = dto.recovery.takeIfNotTemplate()?.cleanHtml(),
            notes = dto.notes.takeIfNotTemplate()?.cleanHtml()
                .extractNotes(),
            active = dto.active.takeIfNotTemplate()?.cleanHtml(),
            guard = dto.guard.takeIfNotTemplate(),
            cancel = dto.cancel.takeIfNotTemplate(),
            invulnerability = dto.invuln.takeIfNotTemplate()?.cleanHtml(),

            aliases = dto.formAliases(type),

            urls = Move.Urls(
                characterImage = characterData.imageUrl,
                moveImageList = dto.images
                    .orEmpty()
                    .split(",")
                    .map { it.trim() }
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                hitboxImageList = dto.hitboxes
                    .orEmpty()
                    .split(",")
                    .map { it.trim() }
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                wikiUrl = formMoveWikiUrl(
                    gameId = gameId,
                    charName = dto.chara,
                    input = dto.input,
                    name = dto.name,
                )
            ),

            sf6Properties = Move.SF6Properties(
                type = type,
                images = dto.images.takeIfNotTemplate()
                    ?.split(",")
                    ?.map { it.trim() },
                chip = dto.chip.takeIfNotTemplate(),
                dmgScaling = dto.dmgScaling.takeIfNotTemplate(),
                total = dto.total.takeIfNotTemplate(),
                hitConfirm = dto.hitconfirm.takeIfNotTemplate(),
                punishAdv = dto.punishAdv.takeIfNotTemplate()?.cleanHtml(),  
                perfParryAdv = dto.perfParryAdv.takeIfNotTemplate()?.cleanHtml(),  
                DRcOH = dto.DRcancelHit.takeIfNotTemplate()?.cleanHtml(),  
                DRcOB = dto.DRcancelBlk.takeIfNotTemplate()?.cleanHtml(),  
                DROH = dto.afterDRHit.takeIfNotTemplate()?.cleanHtml(),  
                DROB = dto.afterDRBlk.takeIfNotTemplate()?.cleanHtml(),
                hitStun = dto.hitstun.takeIfNotTemplate()?.cleanHtml(),  
                blockStun = dto.blockstun.takeIfNotTemplate()?.cleanHtml(),  
                hitStop = dto.hitstop.takeIfNotTemplate()?.cleanHtml(),  
                driveDmgOnBlock = dto.driveDmgBlk.takeIfNotTemplate(),
                driveDmgOnHit = dto.driveDmgHit.takeIfNotTemplate(),
                driveGain = dto.driveGain.takeIfNotTemplate(),
                superGainOnHit = dto.superGainHit.takeIfNotTemplate(),
                superGainOnBlock = dto.superGainBlk.takeIfNotTemplate(),
                armor = dto.armor.takeIfNotTemplate(),
                jugStart = dto.jugStart.takeIfNotTemplate()?.cleanHtml(),  
                jugIncrease = dto.jugIncrease.takeIfNotTemplate()?.cleanHtml(),  
                jugLimit = dto.jugLimit.takeIfNotTemplate(),
                projectileSpeed = dto.projSpeed.takeIfNotTemplate(),
                attackRange = dto.atkRange.takeIfNotTemplate(),
            ),
            mkProperties = Move.MKProperties(
                moveType = dto.moveType.takeIfNotTemplate(),
                cost = dto.moveType
                    .split(",")
                    .filterNot { it.takeIfNotTemplate() == null }
            )
        )

        move
    }
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

//https://wiki.supercombo.gg/w/Street_Fighter_6/Blanka#Electric_Thunder_(214P)
internal fun formMoveWikiUrl(
    gameId: String,
    charName: String,
    input: String,
    name: String?,
): String {
    val moveId = if (name.isNullOrBlank().not()) {
        "${name.replace(" ", "_")}_($input)"
    } else {
        input
    }
    val charQueryName = charName.replace(" ", "_")

    return when (gameId) {
        Game.StreetFighter6.id -> {
            "${WIKI_BASE_URL}/$gameId/$charQueryName#$moveId"
        }
        Game.MK1.id -> {
            "${WIKI_BASE_URL}/$gameId/$charQueryName/Data#$input"
        }
        else -> WIKI_BASE_URL
    }
}

private fun MoveDto.formAliases(type: Move.SF6Properties.Type?): List<String> {
    val alias = when (type) {
        Move.SF6Properties.Type.SUPER -> formSuperLevel(moveId, superGainHit)
        else -> this.input.formMotionInput()
    }

    return alias?.let { listOf(it) } ?: listOf()
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
        this.startsWith("214") -> this.replaceFirst("214", "qcb")
        this.startsWith("236") -> this.replaceFirst("236", "qcf")
        this.startsWith("623") -> this.replaceFirst("623", "dp")
        this.startsWith("421") -> this.replaceFirst("421", "bdp")
        this.startsWith("360") -> this.replaceFirst("360", "spd")
        else -> return null
    }

    return motion
}
