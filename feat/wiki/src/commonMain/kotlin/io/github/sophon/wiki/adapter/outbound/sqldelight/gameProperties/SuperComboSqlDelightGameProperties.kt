package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.AVLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MKCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MKMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SFCharProperties
import io.github.sophon.wiki.application.domain.model.wiki.Game
import io.github.sophon.wiki.data.superCombo.Avl_move
import io.github.sophon.wiki.data.superCombo.Mk1_character
import io.github.sophon.wiki.data.superCombo.Mk1_move
import io.github.sophon.wiki.data.superCombo.Street_fighter6_character
import io.github.sophon.wiki.data.superCombo.Street_fighter6_move

internal class SuperComboSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) {
        when (properties) {
            is SFCharProperties -> database.streetFighter6CharacterQueries.upsert(
                character_id = characterRowId,
                fwd_walk_spd = properties.fwdWalkSpd,
                bwd_walk_spd = properties.bwdWalkSpd,
                fwd_dash_spd = properties.fwdDashSpd,
                bwd_dash_spd = properties.bwdDashSpd,
                fwd_dash_dist = properties.fwdDashDist,
                bwd_dash_dist = properties.bwdDashDist,
                d_rush_min = properties.dRushMin,
                d_rush_block = properties.dRushBlock,
                d_rush_max = properties.dRushMax,
                throw_range = properties.throwRange,
                throw_hurtbox = properties.throwHurtbox,
                jump_spd = properties.jumpSpd,
                jump_apex = properties.jumpApex,
                fwd_jump_dist = properties.fwdJumpDist,
                bwd_jump_dist = properties.bwdJumpDist,
            )

            is MKCharProperties -> database.mk1CharacterQueries.upsert(
                character_id = characterRowId,
                hp_mod = properties.hpMod,
                throw_dmg = properties.throwDmg,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is SF6MoveProperties -> database.streetFighter6MoveQueries.upsert(
                move_id = moveRowId,
                images = properties.images,
                chip = properties.chip,
                dmg_scaling = properties.dmgScaling,
                total = properties.total,
                hit_confirm = properties.hitConfirm,
                punish_adv = properties.punishAdv,
                perf_parry_adv = properties.perfParryAdv,
                drc_oh = properties.DRcOH,
                drc_ob = properties.DRcOB,
                dr_oh = properties.DROH,
                dr_ob = properties.DROB,
                hit_stun = properties.hitStun,
                block_stun = properties.blockStun,
                hit_stop = properties.hitStop,
                drive_dmg_on_block = properties.driveDmgOnBlock,
                drive_dmg_on_hit = properties.driveDmgOnHit,
                drive_gain = properties.driveGain,
                super_gain_on_hit = properties.superGainOnHit,
                super_gain_on_block = properties.superGainOnBlock,
                armor = properties.armor,
                airborne = properties.airborne,
                jug_start = properties.jugStart,
                jug_increase = properties.jugIncrease,
                jug_limit = properties.jugLimit,
                projectile_speed = properties.projectileSpeed,
                attack_range = properties.attackRange,
            )

            is MKMoveProperties -> database.mk1MoveQueries.upsert(
                move_id = moveRowId,
                cost = properties.cost,
                chip = properties.chip,
                flawless_block_adv = properties.flawlessBlockAdv,
                hit_cancel_adv = properties.hitCancelAdv,
                block_cancel_adv = properties.blockCancelAdv,
                punish = properties.punish,
            )

            is AVLMoveProperties -> database.avlMoveQueries.upsert(
                move_id = moveRowId,
                chi_damage = properties.chiDamage,
                flow = properties.flow,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> {
        val propertiesByCharacterRowId: Map<Long, CharacterGameProperties> = when (game) {
            Game.StreetFighter6 -> database.streetFighter6CharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            Game.MK1 -> database.mk1CharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByCharacterRowId
    }

    override fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId: Map<Long, MoveGameProperties> = when (characterId.game) {
            Game.StreetFighter6 -> database.streetFighter6MoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.MK1 -> database.mk1MoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.AVL -> database.avlMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "SuperComboSqlDelightGameProperties"
    }
}

private fun Street_fighter6_character.toDomain(): SFCharProperties {
    val properties = SFCharProperties(
        fwdWalkSpd = fwd_walk_spd,
        bwdWalkSpd = bwd_walk_spd,
        fwdDashSpd = fwd_dash_spd,
        bwdDashSpd = bwd_dash_spd,
        fwdDashDist = fwd_dash_dist,
        bwdDashDist = bwd_dash_dist,
        dRushMin = d_rush_min,
        dRushBlock = d_rush_block,
        dRushMax = d_rush_max,
        throwRange = throw_range,
        throwHurtbox = throw_hurtbox,
        jumpSpd = jump_spd,
        jumpApex = jump_apex,
        fwdJumpDist = fwd_jump_dist,
        bwdJumpDist = bwd_jump_dist,
    )
    return properties
}

private fun Mk1_character.toDomain(): MKCharProperties {
    val properties = MKCharProperties(
        hpMod = hp_mod,
        throwDmg = throw_dmg,
    )
    return properties
}

private fun Street_fighter6_move.toDomain(): SF6MoveProperties {
    val properties = SF6MoveProperties(
        images = images,
        chip = chip,
        dmgScaling = dmg_scaling,
        total = total,
        hitConfirm = hit_confirm,
        punishAdv = punish_adv,
        perfParryAdv = perf_parry_adv,
        DRcOH = drc_oh,
        DRcOB = drc_ob,
        DROH = dr_oh,
        DROB = dr_ob,
        hitStun = hit_stun,
        blockStun = block_stun,
        hitStop = hit_stop,
        driveDmgOnBlock = drive_dmg_on_block,
        driveDmgOnHit = drive_dmg_on_hit,
        driveGain = drive_gain,
        superGainOnHit = super_gain_on_hit,
        superGainOnBlock = super_gain_on_block,
        armor = armor,
        airborne = airborne,
        jugStart = jug_start,
        jugIncrease = jug_increase,
        jugLimit = jug_limit,
        projectileSpeed = projectile_speed,
        attackRange = attack_range,
    )
    return properties
}

private fun Mk1_move.toDomain(): MKMoveProperties {
    val properties = MKMoveProperties(
        cost = cost,
        chip = chip,
        flawlessBlockAdv = flawless_block_adv,
        hitCancelAdv = hit_cancel_adv,
        blockCancelAdv = block_cancel_adv,
        punish = punish,
    )
    return properties
}

private fun Avl_move.toDomain(): AVLMoveProperties {
    val properties = AVLMoveProperties(
        chiDamage = chi_damage,
        flow = flow,
    )
    return properties
}
