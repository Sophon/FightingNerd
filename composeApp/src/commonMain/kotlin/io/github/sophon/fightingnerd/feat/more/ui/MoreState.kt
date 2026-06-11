package io.github.sophon.fightingnerd.feat.more.ui

import io.github.sophon.fightingnerd.feat.more.model.DonationMethod
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.theme.ThemeMode

internal data class MoreState(
    val items: List<MoreItem> = MoreItem.entries,

    val themeSelectorDialog: ThemeSelectorDialog? = null,
    val donationSelectorDialog: DonationDialog? = null,
) {
    data class ThemeSelectorDialog(
        val themeModeLists: List<ThemeMode> = ThemeMode.entries,
    )

    data class DonationDialog(
        val methodList: List<DonationMethod> = DonationMethod.entries,
    )
}
