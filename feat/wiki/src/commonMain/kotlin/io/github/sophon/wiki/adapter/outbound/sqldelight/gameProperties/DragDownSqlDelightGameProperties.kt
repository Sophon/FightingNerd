package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.model.CharacterGameProperties
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.MoveGameProperties
import io.github.sophon.wiki.model.game.Roa2CharProperties
import io.github.sophon.wiki.model.game.Roa2MoveProperties
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.data.dragDown.Roa2_character
import io.github.sophon.wiki.data.dragDown.Roa2_move

internal class DragDownSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) {
        when (properties) {
            is Roa2CharProperties -> database.roa2CharacterQueries.upsert(
                character_id = characterRowId,
                dacus_speed_multiplier = properties.dacusSpeedMultiplier,
                weight = properties.weight,
                friction_ground = properties.frictionGround,
                friction_air = properties.frictionAir,
                dash_frames = properties.dashFrames,
                dash_speed = properties.dashSpeed,
                dash_acceleration = properties.dashAcceleration,
                run_speed_max = properties.runSpeedMax,
                run_turn_acceleration = properties.runTurnAcceleration,
                run_turn_frames = properties.runTurnFrames,
                walk_acceleration_max = properties.walkAccelerationMax,
                walk_speed_max = properties.walkSpeedMax,
                gravity = properties.gravity,
                hitstun_gravity = properties.hitstunGravity,
                fall_speed_max = properties.fallSpeedMax,
                fast_fall_speed = properties.fastFallSpeed,
                air_acceleration = properties.airAcceleration,
                air_speed_horizontal_max = properties.airSpeedHorizontalMax,
                jump_speed_horizontal_max = properties.jumpSpeedHorizontalMax,
                full_hop_speed = properties.fullHopSpeed,
                short_hop_speed = properties.shortHopSpeed,
                double_jump_speed = properties.doubleJumpSpeed,
                double_jump_max_horizontal_speed = properties.doubleJumpMaxHorizontalSpeed,
                air_dodge_speed = properties.airDodgeSpeed,
                air_dodge_friction = properties.airDodgeFriction,
                roll_speed = properties.rollSpeed,
                shield_size_multiplier = properties.shieldSizeMultiplier,
                ledge_stand_speed = properties.ledgeStandSpeed,
                ledge_roll_speed = properties.ledgeRollSpeed,
                ledge_jump_max_horizontal_air_speed = properties.ledgeJumpMaxHorizontalAirSpeed,
                getup_roll_speed = properties.getupRollSpeed,
                tech_roll_speed = properties.techRollSpeed,
                wall_jump_speed_y = properties.wallJumpSpeedY,
                wall_jump_speed_x = properties.wallJumpSpeedX,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is Roa2MoveProperties -> database.roa2MoveQueries.upsert(
                move_id = moveRowId,
                mode = properties.mode,
                caption = properties.caption,
                hitbox_caption = properties.hitboxCaption,
                startup_notes = properties.startupNotes,
                total_active_notes = properties.totalActiveNotes,
                endlag_notes = properties.endlagNotes,
                cancel_notes = properties.cancelNotes,
                landing_lag = properties.landingLag,
                landing_lag_notes = properties.landingLagNotes,
                iasa = properties.iasa,
                iasa_notes = properties.iasaNotes,
                total_duration = properties.totalDuration,
                total_duration_notes = properties.totalDurationNotes,
                ledge_grab_frame = properties.ledgeGrabFrame,
                ledge_grab_frame_notes = properties.ledgeGrabFrameNotes,
                hit_id = properties.hitID,
                hit_move_id = properties.hitMoveID,
                hit_name = properties.hitName,
                hit_active = properties.hitActive,
                custom_shield_safety = properties.customShieldSafety,
                unique_field = properties.uniqueField,
                article_id = properties.articleID,
                notes = properties.notes,
                adv_notes = properties.advNotes,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> {
        val propertiesByCharacterRowId = database.roa2CharacterQueries
            .selectAll()
            .executeAsList()
            .associate { row -> row.character_id to row.toDomain() }
        return propertiesByCharacterRowId
    }

    override fun loadMoveProperties(characterId: CharacterId): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId = database.roa2MoveQueries
            .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
            .executeAsList()
            .associate { row -> row.move_id to row.toDomain() }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "DragDownSqlDelightGameProperties"
    }
}

private fun Roa2_character.toDomain(): Roa2CharProperties {
    val properties = Roa2CharProperties(
        dacusSpeedMultiplier = dacus_speed_multiplier,
        weight = weight,
        frictionGround = friction_ground,
        frictionAir = friction_air,
        dashFrames = dash_frames,
        dashSpeed = dash_speed,
        dashAcceleration = dash_acceleration,
        runSpeedMax = run_speed_max,
        runTurnAcceleration = run_turn_acceleration,
        runTurnFrames = run_turn_frames,
        walkAccelerationMax = walk_acceleration_max,
        walkSpeedMax = walk_speed_max,
        gravity = gravity,
        hitstunGravity = hitstun_gravity,
        fallSpeedMax = fall_speed_max,
        fastFallSpeed = fast_fall_speed,
        airAcceleration = air_acceleration,
        airSpeedHorizontalMax = air_speed_horizontal_max,
        jumpSpeedHorizontalMax = jump_speed_horizontal_max,
        fullHopSpeed = full_hop_speed,
        shortHopSpeed = short_hop_speed,
        doubleJumpSpeed = double_jump_speed,
        doubleJumpMaxHorizontalSpeed = double_jump_max_horizontal_speed,
        airDodgeSpeed = air_dodge_speed,
        airDodgeFriction = air_dodge_friction,
        rollSpeed = roll_speed,
        shieldSizeMultiplier = shield_size_multiplier,
        ledgeStandSpeed = ledge_stand_speed,
        ledgeRollSpeed = ledge_roll_speed,
        ledgeJumpMaxHorizontalAirSpeed = ledge_jump_max_horizontal_air_speed,
        getupRollSpeed = getup_roll_speed,
        techRollSpeed = tech_roll_speed,
        wallJumpSpeedY = wall_jump_speed_y,
        wallJumpSpeedX = wall_jump_speed_x,
    )
    return properties
}

private fun Roa2_move.toDomain(): Roa2MoveProperties {
    val properties = Roa2MoveProperties(
        mode = mode,
        caption = caption,
        hitboxCaption = hitbox_caption,
        startupNotes = startup_notes,
        totalActiveNotes = total_active_notes,
        endlagNotes = endlag_notes,
        cancelNotes = cancel_notes,
        landingLag = landing_lag,
        landingLagNotes = landing_lag_notes,
        iasa = iasa,
        iasaNotes = iasa_notes,
        totalDuration = total_duration,
        totalDurationNotes = total_duration_notes,
        ledgeGrabFrame = ledge_grab_frame,
        ledgeGrabFrameNotes = ledge_grab_frame_notes,
        hitID = hit_id,
        hitMoveID = hit_move_id,
        hitName = hit_name,
        hitActive = hit_active,
        customShieldSafety = custom_shield_safety,
        uniqueField = unique_field,
        articleID = article_id,
        notes = notes,
        advNotes = adv_notes,
    )
    return properties
}
