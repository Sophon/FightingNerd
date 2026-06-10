package io.github.sophon.fightingnerd.feat.more

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_donate
import fightingnerd.composeapp.generated.resources.more_item
import fightingnerd.composeapp.generated.resources.more_theme
import org.jetbrains.compose.resources.StringResource

enum class MoreItem(val stringResource: StringResource) {
    Theme(Res.string.more_theme),
    FeatureSettings(Res.string.more_item),
    Donate(Res.string.more_donate)
}
