package io.github.sophon.fightingnerd.feat.more.model

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_item_about
import fightingnerd.composeapp.generated.resources.more_item_data_update_settings
import fightingnerd.composeapp.generated.resources.more_item_feature_settings
import org.jetbrains.compose.resources.StringResource

enum class MoreItem(val stringResource: StringResource) {
//    Theme(Res.string.more_item_theme_select),
    FeatureSettings(Res.string.more_item_feature_settings),
    UpdatesSettings(Res.string.more_item_data_update_settings),
    About(Res.string.more_item_about),
}
