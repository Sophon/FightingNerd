package io.github.sophon.cornerman.screens.moveList.domain

import cornerman.composeapp.generated.resources.Res
import cornerman.composeapp.generated.resources.ic_crouch
import cornerman.composeapp.generated.resources.ic_floor_break
import cornerman.composeapp.generated.resources.ic_heat
import cornerman.composeapp.generated.resources.ic_homing
import cornerman.composeapp.generated.resources.ic_jump
import cornerman.composeapp.generated.resources.ic_power_crush
import cornerman.composeapp.generated.resources.ic_throw
import cornerman.composeapp.generated.resources.ic_tornado
import cornerman.composeapp.generated.resources.ic_wall_break
import org.jetbrains.compose.resources.DrawableResource

data class UiMove(
    val id: String,
    val input: String,
    val mandatoryFields: List<Field>,
    val optionalFields: List<Field>,
    val isStance: Boolean = false,
    val notes: List<String> = listOf(),
    val properties: List<Property> = listOf(),
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
            resource = Res.drawable.ic_power_crush,
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
    }
}