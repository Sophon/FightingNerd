package io.github.sophon.fightingnerd.feat.more.ui

import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal data class MoreState(
    val items: ImmutableList<MoreItem> = MoreItem.entries.toImmutableList(),
)
