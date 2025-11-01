package io.github.sophon.cornerman

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

private var koinInitialized = false

fun MainViewController() = ComposeUIViewController {
    if (koinInitialized.not()) {
        Napier.base(DebugAntilog())
        initKoin()
        koinInitialized = true
    }

    App()
}