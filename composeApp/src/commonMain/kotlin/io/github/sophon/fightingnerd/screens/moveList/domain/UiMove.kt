package io.github.sophon.fightingnerd.screens.moveList.domain

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_crouch
import fightingnerd.composeapp.generated.resources.ic_fireball
import fightingnerd.composeapp.generated.resources.ic_floor_break
import fightingnerd.composeapp.generated.resources.ic_heat
import fightingnerd.composeapp.generated.resources.ic_homing
import fightingnerd.composeapp.generated.resources.ic_jump
import fightingnerd.composeapp.generated.resources.ic_shield_full
import fightingnerd.composeapp.generated.resources.ic_shield_half
import fightingnerd.composeapp.generated.resources.ic_throw
import fightingnerd.composeapp.generated.resources.ic_tornado
import fightingnerd.composeapp.generated.resources.ic_wall_break
import org.jetbrains.compose.resources.DrawableResource

data class UiMove(
    val id: String,
    val input: String,
    val mandatoryFields: List<Field>,
    val optionalFields: List<Field>,
    val isStance: Boolean = false,
    val details: List<String> = listOf(),
    val notes: List<String> = listOf(),
    val properties: Set<Property> = setOf(),
) {
    data class Field(
        val title: String,
        val value: String? = null,
    )

    enum class Property(
        val resource: DrawableResource,
        val contentDescription: String? = null,
    ) {
        HEAT(
            resource = Res.drawable.ic_heat,
            contentDescription = "Heat",
        ),
        PC(
            resource = Res.drawable.ic_shield_full,
            contentDescription = "Power crush",
        ),
        HOMING(
            resource = Res.drawable.ic_homing,
            contentDescription = "Homing",
        ),
        TORNADO(
            resource = Res.drawable.ic_tornado,
            contentDescription = "Tornado",
        ),
        THROW(
            resource = Res.drawable.ic_throw,
            contentDescription = "Throw",
        ),
        WALL_BREAK(
            resource = Res.drawable.ic_wall_break,
            contentDescription = "Wall break",
        ),
        FLOOR_BREAK(
            resource = Res.drawable.ic_floor_break,
            contentDescription = "Floor break",
        ),
        LOW_CRUSH(
            resource = Res.drawable.ic_jump,
            contentDescription = "Low crush",
        ),
        HIGH_CRUSH(
            resource = Res.drawable.ic_crouch,
            contentDescription = "High crush",
        ),
        INVULNERABLE(
            resource = Res.drawable.ic_shield_full,
            contentDescription = "Invincible",
        ),
        ARMOR(
            resource = Res.drawable.ic_shield_half,
            contentDescription = "Armor",
        ),
        PROJECTILE(
            resource = Res.drawable.ic_fireball,
            contentDescription = "Projectile",
        )
    }
}