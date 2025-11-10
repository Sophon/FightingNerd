package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.wiki.domain.model.Move

internal fun MoveListResponseDto.toDomain(): List<Move> {
    return cargoQuery.map { wrapper ->
        val dto = wrapper.title

        Move(
            charName = dto.chara,
            id = dto.moveId,
            input = dto.input.lowercase(),
            damage = dto.damage.takeIfNotTemplate(),
            startup = dto.startup.takeIfNotTemplate(),
            onBlock = dto.blockAdv.takeIfNotTemplate(),
            onHit = dto.hitAdv.takeIfNotTemplate(),
            onCH = null, // Not provided by SF6 API
            name = dto.name,
            recovery = dto.recovery.takeIfNotTemplate(),
            notes = dto.notes.takeIfNotTemplate()?.let { listOf(it) } ?: emptyList(),
            t8Properties = null,
            sf6Properties = Move.SF6Properties(
                type = dto.moveType,
                active = dto.active.takeIfNotTemplate(),
                guard = dto.guard.takeIfNotTemplate(),
                images = dto.images.takeIfNotTemplate()?.split(",")?.map { it.trim() },
                hitboxes = dto.hitboxes.takeIfNotTemplate()?.split(",")?.map { it.trim() },
                chip = dto.chip.takeIfNotTemplate(),
                dmgScaling = dto.dmgScaling.takeIfNotTemplate(),
                total = dto.total.takeIfNotTemplate(),
                cancel = dto.cancel.takeIfNotTemplate(),
                hitConfirm = dto.hitconfirm.takeIfNotTemplate(),
                punishAdv = dto.punishAdv.takeIfNotTemplate(),
                perfParryAdv = dto.perfParryAdv.takeIfNotTemplate(),
                DRcOH = dto.DRcancelHit.takeIfNotTemplate(),
                DRcOB = dto.DRcancelBlk.takeIfNotTemplate(),
                DROH = dto.afterDRHit.takeIfNotTemplate(),
                DROB = dto.afterDRBlk.takeIfNotTemplate(),
                hitStun = dto.hitstun.takeIfNotTemplate(),
                blockStun = dto.blockstun.takeIfNotTemplate(),
                hitStop = dto.hitstop.takeIfNotTemplate(),
                driveDmgOnBlock = dto.driveDmgBlk.takeIfNotTemplate(),
                driveDmgOnHit = dto.driveDmgHit.takeIfNotTemplate(),
                driveGain = dto.driveGain.takeIfNotTemplate(),
                superGainOnHit = dto.superGainHit.takeIfNotTemplate(),
                superGainOnBlock = dto.superGainBlk.takeIfNotTemplate(),
                invulnerability = dto.invuln.takeIfNotTemplate(),
                armor = dto.armor.takeIfNotTemplate(),
                airborne = dto.airborne.takeIfNotTemplate(),
                jugStart = dto.jugStart.takeIfNotTemplate(),
                jugIncrease = dto.jugIncrease.takeIfNotTemplate(),
                jugLimit = dto.jugLimit.takeIfNotTemplate(),
                projectileSpeed = dto.projSpeed.takeIfNotTemplate(),
                attackRange = dto.atkRange.takeIfNotTemplate(),
            )
        )
    }
}

private fun String?.takeIfNotTemplate(): String? {
    return this?.takeIf { !it.matches(Regex("\\{\\{\\{.+\\}\\}\\}")) }
}