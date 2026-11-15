package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.wiki.adapter.outbound.ktor.BucketTable
import io.github.sophon.wiki.model.wiki.Game

internal object DragDownTables {
    val characterTableByGame = mapOf(
        Game.ROA2 to BucketTable(name = "character_data_roa2", fieldList = roa2CharacterFieldList),
    )

    val moveTableByGame = mapOf(
        Game.ROA2 to BucketTable(name = "movemode_roa2", fieldList = roa2MoveFieldList),
    )
}


private val roa2CharacterFieldList = listOf(
    "chara",
    "dacus_speed_multiplier",
    "weight",
    "friction_ground",
    "friction_air",
    "dash_frames",
    "dash_speed",
    "dash_acceleration",
    "run_speed_max",
    "run_turn_acceleration",
    "run_turn_frames",
    "walk_acceleration_max",
    "walk_speed_max",
    "gravity",
    "hitstun_gravity",
    "fall_speed_max",
    "hitstun_fall_speed_max",
    "fast_fall_speed",
    "air_acceleration",
    "air_speed_horizontal_max",
    "jump_speed_horizontal_max",
    "full_hop_speed",
    "short_hop_speed",
    "double_jump_speed",
    "double_jump_max_horizontal_speed",
    "air_dodge_speed",
    "air_dodge_friction",
    "roll_speed",
    "shield_size_multiplier",
    "ledge_stand_speed",
    "ledge_roll_speed",
    "ledge_jump_max_horizontal_air_speed",
    "getup_roll_speed",
    "tech_roll_speed",
    "wall_jump_speed_y",
    "wall_jump_speed_x",
)

private val roa2MoveFieldList = listOf(
    "chara",
    "attack",
    "attack_id",
    "mode",
    "image",
    "hitbox",
    "caption",
    "hitbox_caption",
    "startup",
    "startup_notes",
    "total_active",
    "total_active_notes",
    "endlag",
    "endlag_notes",
    "cancel",
    "cancel_notes",
    "landing_lag",
    "landing_lag_notes",
    "iasa",
    "iasa_notes",
    "total_duration",
    "total_duration_notes",
    "ledge_grab_frame",
    "ledge_grab_frame_notes",
    "frame_chart",
    "hit_id",
    "hit_move_id",
    "hit_name",
    "hit_active",
    "custom_shield_safety",
    "unique_field",
    "notes",
    "adv_notes",
    "no_frame_data",
)
