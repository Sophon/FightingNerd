package io.github.sophon.fightingnerd.feat.moveList.model

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import fightingnerd.composeapp.generated.resources.ic_tk_cs
import fightingnerd.composeapp.generated.resources.ic_tk_heat
import fightingnerd.composeapp.generated.resources.ic_tk_homing
import fightingnerd.composeapp.generated.resources.ic_tk_pc
import fightingnerd.composeapp.generated.resources.ic_tk_throw
import org.jetbrains.compose.resources.DrawableResource

internal enum class Property {
    Invincible,
    PowerCrush,
    Homing,
    HighCrush,
    LowCrush,
    Heat,
    Throw,
}

internal fun Property.icon(): DrawableResource {
    return when (this) {
        Property.Invincible -> Res.drawable.compose_multiplatform
        Property.PowerCrush -> Res.drawable.ic_tk_pc
        Property.Homing -> Res.drawable.ic_tk_homing
        Property.HighCrush -> Res.drawable.ic_tk_cs
        Property.LowCrush -> Res.drawable.compose_multiplatform
        Property.Heat -> Res.drawable.ic_tk_heat
        Property.Throw -> Res.drawable.ic_tk_throw
    }
}