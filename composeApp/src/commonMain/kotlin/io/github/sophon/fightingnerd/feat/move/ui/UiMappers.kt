package io.github.sophon.fightingnerd.feat.move.ui

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_tk_cs
import fightingnerd.composeapp.generated.resources.ic_tk_floor
import fightingnerd.composeapp.generated.resources.ic_tk_heat
import fightingnerd.composeapp.generated.resources.ic_tk_homing
import fightingnerd.composeapp.generated.resources.ic_tk_js
import fightingnerd.composeapp.generated.resources.ic_tk_pc
import fightingnerd.composeapp.generated.resources.ic_tk_throw
import fightingnerd.composeapp.generated.resources.ic_tk_wall
import fightingnerd.composeapp.generated.resources.move_list_char_air_accel
import fightingnerd.composeapp.generated.resources.move_list_char_air_bwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_air_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_air_speed
import fightingnerd.composeapp.generated.resources.move_list_char_backdash
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_dash_accel
import fightingnerd.composeapp.generated.resources.move_list_char_dash_frames
import fightingnerd.composeapp.generated.resources.move_list_char_dash_init_spd
import fightingnerd.composeapp.generated.resources.move_list_char_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_dmg_received_mod
import fightingnerd.composeapp.generated.resources.move_list_char_double_jump_height
import fightingnerd.composeapp.generated.resources.move_list_char_drush_max
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_block
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_throw
import fightingnerd.composeapp.generated.resources.move_list_char_full_hop_height
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_ground_friction
import fightingnerd.composeapp.generated.resources.move_list_char_guts
import fightingnerd.composeapp.generated.resources.move_list_char_h_jump_speed
import fightingnerd.composeapp.generated.resources.move_list_char_hitstun_gravity
import fightingnerd.composeapp.generated.resources.move_list_char_hp
import fightingnerd.composeapp.generated.resources.move_list_char_hp_health
import fightingnerd.composeapp.generated.resources.move_list_char_hp_life_points
import fightingnerd.composeapp.generated.resources.move_list_char_hp_mod
import fightingnerd.composeapp.generated.resources.move_list_char_jump_startup
import fightingnerd.composeapp.generated.resources.move_list_char_ki_mod
import fightingnerd.composeapp.generated.resources.move_list_char_max_fall_speed
import fightingnerd.composeapp.generated.resources.move_list_char_max_run_speed
import fightingnerd.composeapp.generated.resources.move_list_char_prejump
import fightingnerd.composeapp.generated.resources.move_list_char_short_hop_height
import fightingnerd.composeapp.generated.resources.move_list_char_throw_dmg
import fightingnerd.composeapp.generated.resources.move_list_char_umo
import fightingnerd.composeapp.generated.resources.move_list_char_weight
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_label_cancel
import fightingnerd.composeapp.generated.resources.move_list_field_label_chi
import fightingnerd.composeapp.generated.resources.move_list_field_label_chip
import fightingnerd.composeapp.generated.resources.move_list_field_label_flow
import fightingnerd.composeapp.generated.resources.move_list_field_label_invulnerability
import fightingnerd.composeapp.generated.resources.move_list_field_label_level
import fightingnerd.composeapp.generated.resources.move_list_field_label_meter
import fightingnerd.composeapp.generated.resources.move_list_field_label_property
import fightingnerd.composeapp.generated.resources.move_list_field_label_recovery
import fightingnerd.composeapp.generated.resources.move_list_field_label_rev_damage
import fightingnerd.composeapp.generated.resources.move_list_field_label_stun
import fightingnerd.composeapp.generated.resources.move_list_field_label_type
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_counter
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.core.util.stripMarkdownLinks
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Field
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.UiCharacter
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.UiMove
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.dreamcancel.integration.model.COTWMoveProperties
import io.github.sophon.dreamcancel.integration.model.KOF15MoveProperties
import io.github.sophon.wikiSuperCombo.integration.model.AVLMoveProperties
import io.github.sophon.wikiSuperCombo.integration.model.MKCharProperties
import io.github.sophon.wikiSuperCombo.integration.model.SF6MoveProperties
import io.github.sophon.wikiSuperCombo.integration.model.SFCharProperties
import io.github.sophon.wikidragdown.integration.model.Roa2CharProperties
import io.github.sophon.wikidustloop.integration.model.BBCharProperties
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.DBFZCharProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRCharProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGCharProperties
import io.github.sophon.wikidustloop.integration.model.MTFSCharProperties
import io.github.sophon.wikimizuumi.integration.model.MBTLMoveProperties
import io.github.sophon.wikimizuumi.integration.model.Uni2CharProperties
import io.github.sophon.wikimizuumi.integration.model.VSAVMoveProperties
import io.github.sophon.wikiwavu.integration.model.T8Properties
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.DrawableResource

internal fun Move.toUiMove(): UiMove {
    val result = UiMove(
        id = id,
        input = input,
        name = name,

        propertySet = buildPropertySet(),
        coreFields = createCoreFields(),
        optionalFields = createOptionalFields(),
        notes = notes.map { it.stripMarkdownLinks() }.toImmutableList(),
        urls = urls.toUiUrl(),
    )
    return result
}

private fun Move.buildPropertySet(): ImmutableSet<Property> {
    val propertySet = buildSet {
        invulnerability?.let { add(Property.Invincible) }
        (gameProperties as? T8Properties)?.let { props ->
            if (props.isHeat) add(Property.Heat)
            if (props.isHoming) add(Property.Homing)
            if (props.isPowerCrush) add(Property.PowerCrush)
            if (props.isHighCrush) add(Property.HighCrush)
            if (props.isLowCrush) add(Property.LowCrush)
            if (props.hasWallInteraction) add(Property.Wall)
            if (props.hasFloorInteraction) add(Property.Floor)
        }
        if (isThrow) { add(Property.Throw) }
    }
    return propertySet.toImmutableSet()
}

private fun Move.createCoreFields(): ImmutableList<Field> {
    val list = buildList {
        add(Field(Res.string.move_list_field_startup, startup))
        add(Field(Res.string.move_list_field_guard, guard?.stripMarkdownLinks()))
        add(Field(Res.string.move_list_field_damage, damage?.stripMarkdownLinks()))
        add(Field(Res.string.move_list_field_on_block, onBlock?.stripMarkdownLinks()))
        add(Field(Res.string.move_list_field_on_hit, onHit?.stripMarkdownLinks()))
        add(Field(Res.string.move_list_field_on_counter, onCH?.stripMarkdownLinks()))
    }
    return list.toImmutableList()
}

private fun Move.createOptionalFields(): ImmutableList<Field> {
    val list = buildList {
        recovery?.let { add(Field(Res.string.move_list_field_label_recovery, it)) }
        cancel?.let { add(Field(Res.string.move_list_field_label_cancel, it)) }
        invulnerability?.let { add(Field(Res.string.move_list_field_label_invulnerability, it)) }

        (gameProperties as? SF6MoveProperties)?.chip?.let { add(Field(Res.string.move_list_field_label_chip, it)) }

        (gameProperties as? KOF15MoveProperties)?.stun?.let { add(Field(Res.string.move_list_field_label_stun, it)) }
        (gameProperties as? COTWMoveProperties)?.revDamage?.let { add(Field(Res.string.move_list_field_label_rev_damage, it)) }

        (gameProperties as? GBVSRMoveProperties)?.meter?.let { add(Field(Res.string.move_list_field_label_meter, it)) }
        (gameProperties as? BBMoveProperties)?.level?.let { add(Field(Res.string.move_list_field_label_level, it)) }
        type?.let { add(Field(Res.string.move_list_field_label_type, it)) }
        (gameProperties as? AVLMoveProperties)?.chiDamage?.let { add(Field(Res.string.move_list_field_label_chi, it)) }
        (gameProperties as? AVLMoveProperties)?.flow?.let { add(Field(Res.string.move_list_field_label_flow, it)) }

        (gameProperties as? MBTLMoveProperties)?.property?.let { add(Field(Res.string.move_list_field_label_property, it)) }
        (gameProperties as? VSAVMoveProperties)?.meter?.let { add(Field(Res.string.move_list_field_label_meter, it)) }
    }

    return list.toImmutableList()
}

internal fun Property.icon(): DrawableResource {
    return when (this) {
        Property.Invincible -> Res.drawable.ic_tk_pc
        Property.PowerCrush -> Res.drawable.ic_tk_pc
        Property.Homing -> Res.drawable.ic_tk_homing
        Property.HighCrush -> Res.drawable.ic_tk_cs
        Property.LowCrush -> Res.drawable.ic_tk_js
        Property.Heat -> Res.drawable.ic_tk_heat
        Property.Throw -> Res.drawable.ic_tk_throw
        Property.Wall -> Res.drawable.ic_tk_wall
        Property.Floor -> Res.drawable.ic_tk_floor
    }
}

private fun Move.Urls.toUiUrl(): UiMove.Urls {
    val url = UiMove.Urls(
        videoUrl = videoUrl,
        hitboxImageList = hitboxImageList.toImmutableList(),
        moveImageList = moveImageList.toImmutableList(),
    )
    return url
}

internal fun Character.toUiCharacter(): UiCharacter {
    val fields = when (val props = gameProperties) {
        is SFCharProperties -> sf6Fields(hp = hp, props = props)
        is MKCharProperties -> mk1Fields(hp = hp, props = props)
        is GGCharProperties -> ggstFields(umo = umo, props = props)
        is BBCharProperties -> bbcfFields(hp = hp, umo = umo, props = props)
        is DBFZCharProperties -> dbfzFields(hp = hp, props = props)
        is GBVSRCharProperties -> gbvsrFields(hp = hp, umo = umo, props = props)
        is MTFSCharProperties -> mtfsFields(umo = umo, props = props)
        is Uni2CharProperties -> uni2Fields(hp = hp, umo = umo, props = props)
        is Roa2CharProperties -> roa2Fields(props = props)
        else -> fallbackFields(hp = hp, umo = umo)
    }
    val uiCharacter = UiCharacter(
        displayName = displayName,
        propertyFields = fields.toImmutableList(),
    )
    return uiCharacter
}

private fun List<String>.toUmoValue(): String? {
    val value = map { it.stripMarkdownLinks() }.joinToString(", ").ifBlank { null }
    return value
}

private fun sf6Fields(hp: String?, props: SFCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_hp_life_points, hp),
        Field(Res.string.move_list_char_fwd_walk_speed, props.fwdWalkSpd),
        Field(Res.string.move_list_char_bwd_walk_speed, props.bwdWalkSpd),
        Field(Res.string.move_list_char_fwd_dash_speed, props.fwdDashSpd),
        Field(Res.string.move_list_char_bwd_dash_speed, props.bwdDashSpd),
        Field(Res.string.move_list_char_fwd_dash_dist, props.fwdDashDist),
        Field(Res.string.move_list_char_bwd_dash_dist, props.bwdDashDist),
        Field(Res.string.move_list_char_drush_min_throw, props.dRushMin),
        Field(Res.string.move_list_char_drush_min_block, props.dRushBlock),
        Field(Res.string.move_list_char_drush_max, props.dRushMax),
    )
    return fields
}

private fun mk1Fields(hp: String?, props: MKCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_hp, hp),
        Field(Res.string.move_list_char_hp_mod, props.hpMod),
        Field(Res.string.move_list_char_throw_dmg, props.throwDmg),
    )
    return fields
}

private fun ggstFields(umo: List<String>, props: GGCharProperties): List<Field> {
    val backdashValue = listOfNotNull(props.bwdDashDuration, props.bwdDashInvulnerability)
        .joinToString("\n")
        .ifBlank { null }
    val fields = listOf(
        Field(Res.string.move_list_char_dmg_received_mod, props.defense),
        Field(Res.string.move_list_char_guts, props.guts),
        Field(Res.string.move_list_char_backdash, backdashValue),
        Field(Res.string.move_list_char_dash_init_spd, props.dashInitialSpd),
        Field(Res.string.move_list_char_dash_accel, props.dashAcceleration),
        Field(Res.string.move_list_char_air_dash_dist, props.adDist),
        Field(Res.string.move_list_char_air_bwd_dash_dist, props.abdDist),
        Field(Res.string.move_list_char_jump_startup, props.prejump),
        Field(Res.string.move_list_char_umo, umo.toUmoValue()),
    )
    return fields
}

private fun bbcfFields(hp: String?, umo: List<String>, props: BBCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_hp_health, hp),
        Field(Res.string.move_list_char_prejump, props.preJump),
        Field(Res.string.move_list_char_backdash, props.backDash),
        Field(Res.string.move_list_char_umo, umo.toUmoValue()),
    )
    return fields
}

private fun dbfzFields(hp: String?, props: DBFZCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_hp, hp),
        Field(Res.string.move_list_char_ki_mod, props.kiMod),
    )
    return fields
}

private fun gbvsrFields(hp: String?, umo: List<String>, props: GBVSRCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_hp, hp),
        Field(Res.string.move_list_char_backdash, props.backdash),
        Field(Res.string.move_list_char_umo, umo.toUmoValue()),
    )
    return fields
}

private fun mtfsFields(umo: List<String>, props: MTFSCharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_backdash, props.backdash),
        Field(Res.string.move_list_char_umo, umo.toUmoValue()),
    )
    return fields
}

private fun uni2Fields(hp: String?, umo: List<String>, props: Uni2CharProperties): List<Field> {
    val backdashValue = listOfNotNull(props.bDashDuration, props.bDashDurationNote)
        .joinToString(" ")
        .ifBlank { null }
    val fields = listOf(
        Field(Res.string.move_list_char_hp_health, hp),
        Field(Res.string.move_list_char_prejump, props.jumpStartup),
        Field(Res.string.move_list_char_backdash, backdashValue),
        Field(Res.string.move_list_char_umo, umo.toUmoValue()),
    )
    return fields
}

private fun roa2Fields(props: Roa2CharProperties): List<Field> {
    val fields = listOf(
        Field(Res.string.move_list_char_weight, props.weight),
        Field(Res.string.move_list_char_hitstun_gravity, props.hitstunGravity),
        Field(Res.string.move_list_char_max_fall_speed, props.fallSpeedMax),
        Field(Res.string.move_list_char_dash_speed, props.dashSpeed),
        Field(Res.string.move_list_char_dash_frames, props.dashFrames),
        Field(Res.string.move_list_char_max_run_speed, props.runSpeedMax),
        Field(Res.string.move_list_char_ground_friction, props.frictionGround),
        Field(Res.string.move_list_char_h_jump_speed, props.jumpSpeedHorizontalMax),
        Field(Res.string.move_list_char_air_speed, props.airSpeedHorizontalMax),
        Field(Res.string.move_list_char_air_accel, props.airAcceleration),
        Field(Res.string.move_list_char_short_hop_height, props.shortHopSpeed),
        Field(Res.string.move_list_char_full_hop_height, props.fullHopSpeed),
        Field(Res.string.move_list_char_double_jump_height, props.doubleJumpSpeed),
    )
    return fields
}

private fun fallbackFields(hp: String?, umo: List<String>): List<Field> {
    val fields = buildList {
        if (hp != null) add(Field(Res.string.move_list_char_hp, hp))
        if (umo.isNotEmpty()) add(Field(Res.string.move_list_char_umo, umo.toUmoValue()))
    }
    return fields
}
