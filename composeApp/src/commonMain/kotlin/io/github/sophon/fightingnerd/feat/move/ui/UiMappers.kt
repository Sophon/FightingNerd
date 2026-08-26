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
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.wikidustloop.integration.model.BBMoveProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRMoveProperties
import io.github.sophon.wikiSuperCombo.integration.model.AVLProperties
import io.github.sophon.wikiSuperCombo.integration.model.SF6MoveProperties
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

private fun Move.createCoreFields(): ImmutableList<UiMove.Field> {
    val list = buildList {
        add(UiMove.Field(Res.string.move_list_field_startup, startup))
        add(UiMove.Field(Res.string.move_list_field_guard, guard?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_damage, damage?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_block, onBlock?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_hit, onHit?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_counter, onCH?.stripMarkdownLinks()))
    }
    return list.toImmutableList()
}

private fun Move.createOptionalFields(): ImmutableList<UiMove.Field> {
    val list = buildList {
        recovery?.let { add(UiMove.Field(Res.string.move_list_field_label_recovery, it)) }
        cancel?.let { add(UiMove.Field(Res.string.move_list_field_label_cancel, it)) }
        invulnerability?.let { add(UiMove.Field(Res.string.move_list_field_label_invulnerability, it)) }

        (gameProperties as? SF6MoveProperties)?.chip?.let { add(UiMove.Field(Res.string.move_list_field_label_chip, it)) }

        koF15Properties?.stun?.let { add(UiMove.Field(Res.string.move_list_field_label_stun, it)) }
        cotwProperties?.revDamage?.let { add(UiMove.Field(Res.string.move_list_field_label_rev_damage, it)) }

        (gameProperties as? GBVSRMoveProperties)?.meter?.let { add(UiMove.Field(Res.string.move_list_field_label_meter, it)) }
        (gameProperties as? BBMoveProperties)?.level?.let { add(UiMove.Field(Res.string.move_list_field_label_level, it)) }
        (gameProperties as? BBMoveProperties)?.type?.let { add(UiMove.Field(Res.string.move_list_field_label_type, it)) }
        (gameProperties as? AVLProperties)?.chiDamage?.let { add(UiMove.Field(Res.string.move_list_field_label_chi, it)) }
        (gameProperties as? AVLProperties)?.flow?.let { add(UiMove.Field(Res.string.move_list_field_label_flow, it)) }

        mbProperties?.property?.let { add(UiMove.Field(Res.string.move_list_field_label_property, it)) }
        vsavProperties?.meter?.let { add(UiMove.Field(Res.string.move_list_field_label_meter, it)) }
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
