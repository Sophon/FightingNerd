package io.github.sophon.fightingnerd.feat.more.ui

import io.github.sophon.fightingnerd.feat.more.model.MoreItem

internal data class MoreState(
    val items: List<MoreItem> = MoreItem.entries,
)
