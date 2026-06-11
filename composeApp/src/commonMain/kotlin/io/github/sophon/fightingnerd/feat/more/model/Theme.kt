package io.github.sophon.fightingnerd.feat.more.model

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_theme_dialog_dark
import fightingnerd.composeapp.generated.resources.more_theme_dialog_light
import fightingnerd.composeapp.generated.resources.more_theme_dialog_system
import org.jetbrains.compose.resources.StringResource

internal enum class Theme(val stringResource: StringResource) {
    System(Res.string.more_theme_dialog_system),
    Dark(Res.string.more_theme_dialog_dark),
    Light(Res.string.more_theme_dialog_light),
}
