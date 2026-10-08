package io.github.sophon.fightingnerd.adapter.inbound.navigation

import io.github.sophon.fightingnerd.adapter.inbound.coreUi.FlexibleIcon
import org.jetbrains.compose.resources.StringResource

internal data class BottomBarItem(
    val label: StringResource,
    val icon: FlexibleIcon,
    val destination: Destination,
)
