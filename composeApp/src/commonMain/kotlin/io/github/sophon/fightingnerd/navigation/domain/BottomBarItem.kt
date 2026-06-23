package io.github.sophon.fightingnerd.navigation.domain

import io.github.sophon.fightingnerd.core.ui.FlexibleIcon
import org.jetbrains.compose.resources.StringResource

internal data class BottomBarItem(
    val label: StringResource,
    val icon: FlexibleIcon,
    val destination: Destination.TopLevelDestination,
)
