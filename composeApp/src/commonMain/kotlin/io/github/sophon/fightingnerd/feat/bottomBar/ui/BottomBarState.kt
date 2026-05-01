package io.github.sophon.fightingnerd.feat.bottomBar.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.bottom_bar_item_character_list
import fightingnerd.composeapp.generated.resources.bottom_bar_item_saved
import fightingnerd.composeapp.generated.resources.bottom_bar_item_search
import io.github.sophon.fightingnerd.feat.bottomBar.model.BottomBarItem
import io.github.sophon.fightingnerd.feat.core.FlexibleIcon

internal data class BottomBarState(
    val itemList: List<BottomBarItem> = listOf(),
    val selectedItemIndex: Int = 0,
) {
    companion object {
        val DEFAULT = BottomBarState(
            itemList = listOf(
                BottomBarItem(
                    label = Res.string.bottom_bar_item_character_list,
                    icon = FlexibleIcon.Vector(Icons.Default.GridView),
                ),
                BottomBarItem(
                    label = Res.string.bottom_bar_item_search,
                    icon = FlexibleIcon.Vector(Icons.Default.Search),
                ),
                BottomBarItem(
                    label = Res.string.bottom_bar_item_saved,
                    icon = FlexibleIcon.Vector(Icons.Default.Bookmark),
                ),
            )
        )
    }
}