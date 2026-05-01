package io.github.sophon.fightingnerd.feat.bottomBar.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

internal data class BottomBarItem(
    val label: StringResource,
    val icon: DrawableResource,
    //TODO: navigation route
)
