package io.github.sophon.fightingnerd.feat.bottomBar

import io.github.sophon.fightingnerd.feat.bottomBar.model.BottomBarItem

internal data class BottomBarState(
    val itemList: List<BottomBarItem> = listOf(),
    val selectedItemIndex: Int = 0,
)