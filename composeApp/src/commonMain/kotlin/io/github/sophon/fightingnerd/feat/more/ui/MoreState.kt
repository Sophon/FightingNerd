package io.github.sophon.fightingnerd.feat.more.ui

import io.github.sophon.fightingnerd.feat.more.model.DonationMethod
import io.github.sophon.fightingnerd.feat.more.model.MoreItem

internal data class MoreState(
    val items: List<MoreItem> = MoreItem.entries,

    val donationSelectorDialog: DonationDialog = DonationDialog(),
) {
    data class DonationDialog(
        val methodList: List<DonationMethod> = DonationMethod.entries,
        val isVisible: Boolean = false,
    )
}
