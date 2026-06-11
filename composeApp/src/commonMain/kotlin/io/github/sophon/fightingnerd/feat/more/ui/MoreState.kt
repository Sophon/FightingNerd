package io.github.sophon.fightingnerd.feat.more.ui

import io.github.sophon.fightingnerd.feat.more.model.DonationMethod
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.feat.more.model.Theme

internal data class MoreState(
    val items: List<MoreItem> = MoreItem.entries,

    val themeSelectorDialog: ThemeSelectorDialog? = null,
    val donationMethod: DonationMethod? = null,
) {
    data class ThemeSelectorDialog(
        val themeList: List<Theme> = Theme.entries,
    )

    data class DonationDialog(
        val methodList: List<DonationMethod> = DonationMethod.entries,
    )
}
