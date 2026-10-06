package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.data.dustLoop.Bbcf_character
import io.github.sophon.wiki.data.dustLoop.Bbcf_move
import io.github.sophon.wiki.data.dustLoop.Dbfz_character
import io.github.sophon.wiki.data.dustLoop.Dbfz_move
import io.github.sophon.wiki.data.dustLoop.Gbvsr_character
import io.github.sophon.wiki.data.dustLoop.Gbvsr_move
import io.github.sophon.wiki.data.dustLoop.Ggst_character
import io.github.sophon.wiki.data.dustLoop.Ggst_move
import io.github.sophon.wiki.data.dustLoop.Mtfs_character
import io.github.sophon.wiki.data.dustLoop.Mtfs_move
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.game.BBCharProperties
import io.github.sophon.wiki.model.game.BBMoveProperties
import io.github.sophon.wiki.model.game.DBFZCharProperties
import io.github.sophon.wiki.model.game.DBFZMoveProperties
import io.github.sophon.wiki.model.game.GBVSRCharProperties
import io.github.sophon.wiki.model.game.GBVSRMoveProperties
import io.github.sophon.wiki.model.game.GGCharProperties
import io.github.sophon.wiki.model.game.GGMoveProperties
import io.github.sophon.wiki.model.game.MTFSCharProperties
import io.github.sophon.wiki.model.game.MTFSMoveProperties
import io.github.sophon.wiki.model.wiki.Game

internal class DustLoopSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) {
        when (properties) {
            is GGCharProperties -> database.ggstCharacterQueries.upsert(
                character_id = characterRowId,
                defense = properties.defense,
                guts = properties.guts,
                guard_balance = properties.guardBalance,
                prejump = properties.prejump,
                bwd_dash = properties.bwdDash,
                bwd_dash_duration = properties.bwdDashDuration,
                bwd_dash_invulnerability = properties.bwdDashInvulnerability,
                bwd_dash_airborne = properties.bwdDashAirborne,
                bwd_dash_dist = properties.bwdDashDist,
                fwd_dash = properties.fwdDash,
                jump_duration = properties.jumpDuration,
                high_jump_duration = properties.highJumpDuration,
                jump_height = properties.jumpHeight,
                high_jump_height = properties.highJumpHeight,
                earliest_iad = properties.earliestIAD,
                ad_duration = properties.adDuration,
                abd_duration = properties.abdDuration,
                ad_dist = properties.adDist,
                abd_dist = properties.abdDist,
                movement_tension = properties.movementTension,
                jump_tension = properties.jumpTension,
                air_dash_tension = properties.airDashTension,
                walk_spd = properties.walkSpd,
                bwd_walk_spd = properties.bwdWalkSpd,
                dash_initial_spd = properties.dashInitialSpd,
                dash_acceleration = properties.dashAcceleration,
                dash_friction = properties.dashFriction,
                jump_gravity = properties.jumpGravity,
                high_jump_gravity = properties.highJumpGravity,
                boost_attack = properties.boostAttack,
                boost_defense = properties.boostDefense,
            )

            is DBFZCharProperties -> database.dbfzCharacterQueries.upsert(
                character_id = characterRowId,
                ki_mod = properties.kiMod,
            )

            is GBVSRCharProperties -> database.gbvsrCharacterQueries.upsert(
                character_id = characterRowId,
                jump_pre = properties.jump?.pre,
                jump_forward_distance = properties.jump?.forwardDistance,
                jump_super_forward_distance = properties.jump?.superForwardDistance,
                jump_back_distance = properties.jump?.backDistance,
                jump_super_back_distance = properties.jump?.superBackDistance,
                jump_gravity = properties.jump?.gravity,
                jump_super_gravity = properties.jump?.superGravity,
                jump_super_height = properties.jump?.superHeight,
                backdash = properties.backdash,
                walk_speed = properties.walkSpeed,
                walk_speed_back = properties.walkSpeedBack,
                dash_initial = properties.dashInitial,
                dash_acceleration = properties.dashAcceleration,
                close_range_l = properties.closeRange?.l,
                close_range_m = properties.closeRange?.m,
                close_range_h = properties.closeRange?.h,
            )

            is BBCharProperties -> database.bbcfCharacterQueries.upsert(
                character_id = characterRowId,
                pre_jump = properties.preJump,
                back_dash = properties.backDash,
                forward_dash = properties.forwardDash,
            )

            is MTFSCharProperties -> database.mtfsCharacterQueries.upsert(
                character_id = characterRowId,
                prejump = properties.prejump,
                backdash = properties.backdash,
                team = properties.team,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is GGMoveProperties -> database.ggstMoveQueries.upsert(
                move_id = moveRowId,
                risc_gain = properties.riscGain,
                risc_loss = properties.riscLoss,
                wall_damage = properties.wallDamage,
                input_tension = properties.inputTension,
                chip_ratio = properties.chipRatio,
                otg_type = properties.otgType,
                prorate = properties.prorate,
                level = properties.level,
            )

            is DBFZMoveProperties -> database.dbfzMoveQueries.upsert(
                move_id = moveRowId,
                attribute = properties.attribute,
                smash = properties.smash,
                ki_gain = properties.kiGain,
                prorate = properties.prorate,
                block_stun = properties.blockStun,
                ground_hit = properties.groundHit,
                air_hit = properties.airHit,
                level = properties.level,
            )

            is GBVSRMoveProperties -> database.gbvsrMoveQueries.upsert(
                move_id = moveRowId,
                meter = properties.meter,
                level = properties.level,
                cooldown = properties.cooldown,
                cls = properties.cls,
            )

            is BBMoveProperties -> database.bbcfMoveQueries.upsert(
                move_id = moveRowId,
                on_odr = properties.onODR,
                attribute = properties.attribute,
                p1 = properties.p1,
                p2 = properties.p2,
                starter = properties.starter,
                level = properties.level,
                blockstun = properties.blockstun,
                ground_hit = properties.groundHit,
                air_hit = properties.airHit,
                ground_ch = properties.groundCH,
                air_ch = properties.airCH,
                blockstop = properties.blockstop,
                hitstop = properties.hitstop,
                ch_stop = properties.chStop,
                cancel_timing = properties.cancelTiming,
            )

            is MTFSMoveProperties -> database.mtfsMoveQueries.upsert(
                move_id = moveRowId,
                simple_input = properties.simpleInput,
                level = properties.level,
                prorate = properties.prorate,
                meter_gain = properties.meterGain,
                untech_amount = properties.untechAmount,
                hitbox_caption = properties.hitboxCaption,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> {
        val propertiesByCharacterRowId: Map<Long, CharacterGameProperties> = when (game) {
            Game.GGST -> database.ggstCharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            Game.DBFZ -> database.dbfzCharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            Game.GBVSR -> database.gbvsrCharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            Game.BBCF -> database.bbcfCharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            Game.MTFS -> database.mtfsCharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByCharacterRowId
    }

    override fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId: Map<Long, MoveGameProperties> = when (characterId.game) {
            Game.GGST -> database.ggstMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.DBFZ -> database.dbfzMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.GBVSR -> database.gbvsrMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.BBCF -> database.bbcfMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.MTFS -> database.mtfsMoveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "DustLoopSqlDelightGameProperties"
    }
}

private fun Ggst_character.toDomain(): GGCharProperties {
    val properties = GGCharProperties(
        defense = defense,
        guts = guts,
        guardBalance = guard_balance,
        prejump = prejump,
        bwdDash = bwd_dash,
        bwdDashDuration = bwd_dash_duration,
        bwdDashInvulnerability = bwd_dash_invulnerability,
        bwdDashAirborne = bwd_dash_airborne,
        bwdDashDist = bwd_dash_dist,
        fwdDash = fwd_dash,
        jumpDuration = jump_duration,
        highJumpDuration = high_jump_duration,
        jumpHeight = jump_height,
        highJumpHeight = high_jump_height,
        earliestIAD = earliest_iad,
        adDuration = ad_duration,
        abdDuration = abd_duration,
        adDist = ad_dist,
        abdDist = abd_dist,
        movementTension = movement_tension,
        jumpTension = jump_tension,
        airDashTension = air_dash_tension,
        walkSpd = walk_spd,
        bwdWalkSpd = bwd_walk_spd,
        dashInitialSpd = dash_initial_spd,
        dashAcceleration = dash_acceleration,
        dashFriction = dash_friction,
        jumpGravity = jump_gravity,
        highJumpGravity = high_jump_gravity,
        boostAttack = boost_attack,
        boostDefense = boost_defense,
    )
    return properties
}

private fun Dbfz_character.toDomain(): DBFZCharProperties {
    val properties = DBFZCharProperties(
        kiMod = ki_mod,
    )
    return properties
}

// jump and closeRange are flattened into prefixed columns - the remote mapper always builds both, a null one loads back empty
private fun Gbvsr_character.toDomain(): GBVSRCharProperties {
    val properties = GBVSRCharProperties(
        jump = GBVSRCharProperties.Jump(
            pre = jump_pre,
            forwardDistance = jump_forward_distance,
            superForwardDistance = jump_super_forward_distance,
            backDistance = jump_back_distance,
            superBackDistance = jump_super_back_distance,
            gravity = jump_gravity,
            superGravity = jump_super_gravity,
            superHeight = jump_super_height,
        ),
        backdash = backdash,
        walkSpeed = walk_speed,
        walkSpeedBack = walk_speed_back,
        dashInitial = dash_initial,
        dashAcceleration = dash_acceleration,
        closeRange = GBVSRCharProperties.CloseRange(
            l = close_range_l,
            m = close_range_m,
            h = close_range_h,
        ),
    )
    return properties
}

private fun Bbcf_character.toDomain(): BBCharProperties {
    val properties = BBCharProperties(
        preJump = pre_jump,
        backDash = back_dash,
        forwardDash = forward_dash,
    )
    return properties
}

private fun Mtfs_character.toDomain(): MTFSCharProperties {
    val properties = MTFSCharProperties(
        prejump = prejump,
        backdash = backdash,
        team = team,
    )
    return properties
}

private fun Ggst_move.toDomain(): GGMoveProperties {
    val properties = GGMoveProperties(
        riscGain = risc_gain,
        riscLoss = risc_loss,
        wallDamage = wall_damage,
        inputTension = input_tension,
        chipRatio = chip_ratio,
        otgType = otg_type,
        prorate = prorate,
        level = level,
    )
    return properties
}

private fun Dbfz_move.toDomain(): DBFZMoveProperties {
    val properties = DBFZMoveProperties(
        attribute = attribute,
        smash = smash,
        kiGain = ki_gain,
        prorate = prorate,
        blockStun = block_stun,
        groundHit = ground_hit,
        airHit = air_hit,
        level = level,
    )
    return properties
}

private fun Gbvsr_move.toDomain(): GBVSRMoveProperties {
    val properties = GBVSRMoveProperties(
        meter = meter,
        level = level,
        cooldown = cooldown,
        cls = cls,
    )
    return properties
}

private fun Bbcf_move.toDomain(): BBMoveProperties {
    val properties = BBMoveProperties(
        onODR = on_odr,
        attribute = attribute,
        p1 = p1,
        p2 = p2,
        starter = starter,
        level = level,
        blockstun = blockstun,
        groundHit = ground_hit,
        airHit = air_hit,
        groundCH = ground_ch,
        airCH = air_ch,
        blockstop = blockstop,
        hitstop = hitstop,
        chStop = ch_stop,
        cancelTiming = cancel_timing,
    )
    return properties
}

private fun Mtfs_move.toDomain(): MTFSMoveProperties {
    val properties = MTFSMoveProperties(
        simpleInput = simple_input,
        level = level,
        prorate = prorate,
        meterGain = meter_gain,
        untechAmount = untech_amount,
        hitboxCaption = hitbox_caption,
    )
    return properties
}
