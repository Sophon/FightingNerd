package io.github.sophon.wiki.adapter.outbound.sqldelight.gameProperties

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.LazyWikiDB
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.MoveGameProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.MBTLMoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2CharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2MoveProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.VSAVMoveProperties
import io.github.sophon.wiki.data.mizuumi.Mbtl_move
import io.github.sophon.wiki.data.mizuumi.Uni2_character
import io.github.sophon.wiki.data.mizuumi.Uni2_move
import io.github.sophon.wiki.data.mizuumi.Vsav_move

internal class MizuumiSqlDelightGameProperties(
    wikiDatabase: LazyWikiDB,
) : SqlDelightGameProperties {
    private val database by wikiDatabase

    override fun saveCharacterProperties(
        characterRowId: Long,
        properties: CharacterGameProperties,
    ) {
        when (properties) {
            is Uni2CharProperties -> database.uni2CharacterQueries.upsert(
                character_id = characterRowId,
                smart_steer = properties.smartSteer,
                f_walk_speed = properties.fWalkSpeed,
                f_walk_speed_note = properties.fWalkSpeedNote,
                b_walk_speed = properties.bWalkSpeed,
                b_walk_speed_note = properties.bWalkSpeedNote,
                jump_startup = properties.jumpStartup,
                jump_duration = properties.jumpDuration,
                jump_duration_note = properties.jumpDurationNote,
                dash_startup = properties.dashStartup,
                i_dash_speed = properties.iDashSpeed,
                i_dash_speed_note = properties.iDashSpeedNote,
                dash_accel = properties.dashAccel,
                dash_accel_note = properties.dashAccelNote,
                max_dash_speed = properties.maxDashSpeed,
                b_dash_startup = properties.bDashStartup,
                b_dash_duration = properties.bDashDuration,
                b_dash_duration_note = properties.bDashDurationNote,
                b_dash_distance = properties.bDashDistance,
                b_dash_distance_note = properties.bDashDistanceNote,
                b_dash_full_invul_start = properties.bDashFullInvulStart,
                b_dash_full_invul_end = properties.bDashFullInvulEnd,
                b_dash_throw_invul_start = properties.bDashThrowInvulStart,
                b_dash_throw_invul_end = properties.bDashThrowInvulEnd,
                throw_width = properties.throwWidth,
                throw_range = properties.throwRange,
                trait = properties.trait,
                vorpal_trait = properties.vorpalTrait,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun saveMoveProperties(
        moveRowId: Long,
        properties: MoveGameProperties,
    ) {
        when (properties) {
            is MBTLMoveProperties -> database.mbtlMoveQueries.upsert(
                move_id = moveRowId,
                input_info = properties.inputInfo,
                subtitle = properties.subtitle,
                min_damage = properties.minDamage,
                mizuumi_property = properties.mizuumiProperty,
                cost = properties.cost,
                attribute = properties.attribute,
                landing = properties.landing,
                overall = properties.overall,
            )

            is Uni2MoveProperties -> database.uni2MoveQueries.upsert(
                move_id = moveRowId,
                input_info = properties.inputInfo,
                subtitle = properties.subtitle,
                min_damage = properties.minDamage,
                cancel_window = properties.cancelWindow,
                mizuumi_property = properties.mizuumiProperty,
                cost = properties.cost,
                attribute = properties.attribute,
                landing = properties.landing,
                overall = properties.overall,
                assault_adv = properties.assaultAdv,
                blockstun = properties.blockstun,
                ground_hit = properties.groundHit,
                air_hit = properties.airHit,
                ground_ch = properties.groundCH,
                air_ch = properties.airCH,
                hitstop = properties.hitstop,
                ch_stop = properties.CHstop,
                proration = properties.proration,
                combo_p1 = properties.comboP1,
                combo_p2 = properties.comboP2,
            )

            is VSAVMoveProperties -> database.vsavMoveQueries.upsert(
                move_id = moveRowId,
                input_info = properties.inputInfo,
                subtitle = properties.subtitle,
                white_dmg = properties.whiteDmg,
                renda = properties.renda,
                meter = properties.meter,
                reaction = properties.reaction,
                curse_time = properties.curseTime,
            )

            else -> Napier.w(tag = TAG) { "no table for ${properties::class.simpleName}" }
        }
    }

    override fun loadCharacterProperties(game: Game): Map<Long, CharacterGameProperties> {
        val propertiesByCharacterRowId: Map<Long, CharacterGameProperties> = when (game) {
            Game.Uni2 -> database.uni2CharacterQueries
                .selectAll()
                .executeAsList()
                .associate { row -> row.character_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByCharacterRowId
    }

    override fun loadMoveProperties(
        game: Game,
        characterId: CharacterId,
    ): Map<Long, MoveGameProperties> {
        val propertiesByMoveRowId: Map<Long, MoveGameProperties> = when (game) {
            Game.MBTL -> database.mbtlMoveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.Uni2 -> database.uni2MoveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            Game.VSAV -> database.vsavMoveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
                .executeAsList()
                .associate { row -> row.move_id to row.toDomain() }

            else -> emptyMap()
        }
        return propertiesByMoveRowId
    }


    private companion object {
        const val TAG = "MizuumiSqlDelightGameProperties"
    }
}

private fun Uni2_character.toDomain(): Uni2CharProperties {
    val properties = Uni2CharProperties(
        smartSteer = smart_steer,
        fWalkSpeed = f_walk_speed,
        fWalkSpeedNote = f_walk_speed_note,
        bWalkSpeed = b_walk_speed,
        bWalkSpeedNote = b_walk_speed_note,
        jumpStartup = jump_startup,
        jumpDuration = jump_duration,
        jumpDurationNote = jump_duration_note,
        dashStartup = dash_startup,
        iDashSpeed = i_dash_speed,
        iDashSpeedNote = i_dash_speed_note,
        dashAccel = dash_accel,
        dashAccelNote = dash_accel_note,
        maxDashSpeed = max_dash_speed,
        bDashStartup = b_dash_startup,
        bDashDuration = b_dash_duration,
        bDashDurationNote = b_dash_duration_note,
        bDashDistance = b_dash_distance,
        bDashDistanceNote = b_dash_distance_note,
        bDashFullInvulStart = b_dash_full_invul_start,
        bDashFullInvulEnd = b_dash_full_invul_end,
        bDashThrowInvulStart = b_dash_throw_invul_start,
        bDashThrowInvulEnd = b_dash_throw_invul_end,
        throwWidth = throw_width,
        throwRange = throw_range,
        trait = trait,
        vorpalTrait = vorpal_trait,
    )
    return properties
}

private fun Mbtl_move.toDomain(): MBTLMoveProperties {
    val properties = MBTLMoveProperties(
        inputInfo = input_info,
        subtitle = subtitle,
        minDamage = min_damage,
        mizuumiProperty = mizuumi_property,
        cost = cost,
        attribute = attribute,
        landing = landing,
        overall = overall,
    )
    return properties
}

private fun Uni2_move.toDomain(): Uni2MoveProperties {
    val properties = Uni2MoveProperties(
        inputInfo = input_info,
        subtitle = subtitle,
        minDamage = min_damage,
        cancelWindow = cancel_window,
        mizuumiProperty = mizuumi_property,
        cost = cost,
        attribute = attribute,
        landing = landing,
        overall = overall,
        assaultAdv = assault_adv,
        blockstun = blockstun,
        groundHit = ground_hit,
        airHit = air_hit,
        groundCH = ground_ch,
        airCH = air_ch,
        hitstop = hitstop,
        CHstop = ch_stop,
        proration = proration,
        comboP1 = combo_p1,
        comboP2 = combo_p2,
    )
    return properties
}

private fun Vsav_move.toDomain(): VSAVMoveProperties {
    val properties = VSAVMoveProperties(
        inputInfo = input_info,
        subtitle = subtitle,
        whiteDmg = white_dmg,
        renda = renda,
        meter = meter,
        reaction = reaction,
        curseTime = curse_time,
    )
    return properties
}
