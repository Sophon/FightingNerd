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

        Move(
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

            urls = Move.Urls(
                characterImage = characterData.imageUrl,
                hitboxImageList = dto.hitboxes
                    .orEmpty()
                    .split(",")
                    .mapNotNull { imageUrlMap.getOrElse(key = it, defaultValue = { null }) },
                wikiUrl = formMoveWikiUrl(
                    gameId = gameId,
                    charName = dto.chara,
                    input = dto.input,
                    name = dto.name,
                )
            ),

            sf6Properties = Move.SF6Properties(
                type = dto.moveType,
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