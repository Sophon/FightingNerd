package io.github.sophon.fightingnerd.infrastructure

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.mp.KoinPlatform.getKoin

internal actual fun createDataStore(): DataStore<Preferences> {
    val context = getKoin().get<Context>()
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir.resolve("wavu_preferences.preferences_pb")
                .absolutePath
                .toPath()
        }
    )
}