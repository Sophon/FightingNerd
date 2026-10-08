package io.github.sophon.fightingnerd.adapter.inbound.coreUi

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

internal sealed class FlexibleIcon {
    data class Vector(val imageVector: ImageVector) : FlexibleIcon()
    data class Resource(val drawableResource: DrawableResource) : FlexibleIcon()
}