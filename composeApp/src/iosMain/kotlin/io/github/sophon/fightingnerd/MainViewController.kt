package io.github.sophon.fightingnerd

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.feat.payment.initRevenueCat

private var koinInitialized = false

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController {
    if (koinInitialized.not()) {
        Napier.base(DebugAntilog())
        initKoin()
        initRevenueCat(BuildKonfig.REVENUECAT_API_KEY)
        koinInitialized = true
    }

    App()
}