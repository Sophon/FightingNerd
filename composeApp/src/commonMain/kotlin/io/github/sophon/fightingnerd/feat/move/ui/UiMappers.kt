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
import org.jetbrains.compose.resources.DrawableResource

internal fun Move.toUiMove(): UiMove {
    val result = UiMove(
        id = id,
        input = input,
        name = name,

        propertySet = buildSet {
            invulnerability?.let { add(Property.Invincible) }
            t8Properties?.let { props ->
                if (props.isHeat) add(Property.Heat)
                if (props.isHoming) add(Property.Homing)
                if (props.isPowerCrush) add(Property.PowerCrush)
                if (props.isHighCrush) add(Property.HighCrush)
                if (props.isLowCrush) add(Property.LowCrush)
                if (props.hasWallInteraction) add(Property.Wall)
                if (props.hasFloorInteraction) add(Property.Floor)
            }
            if (isThrow) { add(Property.Throw) }
        },
        coreFields = createCoreFields(),
        optionalFields = createOptionalFields(),
        notes = notes.map { it.stripMarkdownLinks() },
        urls = urls.toUiUrl(),
    )
    return result
}

private fun Move.createCoreFields(): List<UiMove.Field> {
    val list = buildList {
        add(UiMove.Field(Res.string.move_list_field_startup, startup))
        add(UiMove.Field(Res.string.move_list_field_guard, guard?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_damage, damage?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_block, onBlock?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_hit, onHit?.stripMarkdownLinks()))
        add(UiMove.Field(Res.string.move_list_field_on_counter, onCH?.stripMarkdownLinks()))
    }
    return list
}

private fun Move.createOptionalFields(): List<UiMove.Field> {
    val list = buildList {
        recovery?.let { add(UiMove.Field(Res.string.move_list_field_label_recovery, it)) }
        cancel?.let { add(UiMove.Field(Res.string.move_list_field_label_cancel, it)) }
        invulnerability?.let { add(UiMove.Field(Res.string.move_list_field_label_invulnerability, it)) }

        sf6Properties?.chip?.let { add(UiMove.Field(Res.string.move_list_field_label_chip, it)) }

        koF15Properties?.stun?.let { add(UiMove.Field(Res.string.move_list_field_label_stun, it)) }
        cotwProperties?.revDamage?.let { add(UiMove.Field(Res.string.move_list_field_label_rev_damage, it)) }

        gbvsrProperties?.meter?.let { add(UiMove.Field(Res.string.move_list_field_label_meter, it)) }
        bbProperties?.level?.let { add(UiMove.Field(Res.string.move_list_field_label_level, it)) }
        bbProperties?.type?.let { add(UiMove.Field(Res.string.move_list_field_label_type, it)) }
        avlProperties?.chiDamage?.let { add(UiMove.Field(Res.string.move_list_field_label_chi, it)) }
        avlProperties?.flow?.let { add(UiMove.Field(Res.string.move_list_field_label_flow, it)) }

        mbProperties?.property?.let { add(UiMove.Field(Res.string.move_list_field_label_property, it)) }
        vsavProperties?.meter?.let { add(UiMove.Field(Res.string.move_list_field_label_meter, it)) }
    }

    return list
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
        videoUrl = videoId,
        hitboxImageList = hitboxImageList,
        moveImageList = moveImageList,
    )
    return url
}
