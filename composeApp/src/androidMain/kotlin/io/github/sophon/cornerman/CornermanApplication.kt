package io.github.sophon.cornerman

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

class CornermanApplication: Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            Napier.base(DebugAntilog())
            androidContext(this@CornermanApplication)
        }
    }
}