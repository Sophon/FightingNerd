package io.github.sophon.fightingnerd

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.adapter.outbound.revenueCat.initRevenueCat
import io.github.sophon.fightingnerd.feat.scheduler.registerBGTask

private var initialized = false

fun iosAppInit() {
    if (initialized) return
    Napier.base(DebugAntilog())
    initKoin()
    initRevenueCat(BuildKonfig.REVENUECAT_API_KEY)
    registerBGTask()
    initialized = true
}
