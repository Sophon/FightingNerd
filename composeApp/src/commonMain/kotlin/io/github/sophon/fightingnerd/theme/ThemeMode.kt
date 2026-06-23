package io.github.sophon.fightingnerd.theme

import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_theme_dialog_dark
import fightingnerd.composeapp.generated.resources.more_theme_dialog_default
import fightingnerd.composeapp.generated.resources.more_theme_dialog_light
import fightingnerd.composeapp.generated.resources.more_theme_dialog_system
import org.jetbrains.compose.resources.StringResource

internal enum class ThemeMode(val stringResource: StringResource) {
    System(Res.string.more_theme_dialog_system),
    Dark(Res.string.more_theme_dialog_dark),
    Light(Res.string.more_theme_dialog_light),

    Default(Res.string.more_theme_dialog_default),
}
