package io.github.sophon.fightingnerd.feat.bottomBar.model

import io.github.sophon.fightingnerd.core.ui.FlexibleIcon
import org.jetbrains.compose.resources.StringResource

internal data class BottomBarItem(
    val label: StringResource,
    val icon: FlexibleIcon,
    //TODO: navigation route
)
