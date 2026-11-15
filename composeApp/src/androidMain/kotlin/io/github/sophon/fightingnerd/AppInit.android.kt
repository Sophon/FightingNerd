package io.github.sophon.fightingnerd

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.adapter.outbound.revenueCat.initRevenueCat
import org.koin.android.ext.koin.androidContext

fun androidAppInit(application: Application) {
    initKoin {
        Napier.base(DebugAntilog())
        androidContext(application)
    }

    initRevenueCat(BuildKonfig.REVENUECAT_API_KEY)
}
