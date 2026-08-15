package io.github.sophon.fightingnerd

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.feat.payment.initRevenueCat
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

internal class FightingNerdApplication: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            Napier.base(DebugAntilog())
            androidContext(this@FightingNerdApplication)
        }

        initRevenueCat(BuildKonfig.REVENUECAT_API_KEY)
    }
}