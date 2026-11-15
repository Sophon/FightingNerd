package io.github.sophon.fightingnerd

import android.app.Application
import org.koin.core.component.KoinComponent

internal class FightingNerdApplication: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        androidAppInit(this)
    }
}
