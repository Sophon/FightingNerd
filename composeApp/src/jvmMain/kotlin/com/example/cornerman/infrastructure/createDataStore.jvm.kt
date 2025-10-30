package com.example.cornerman.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            File(System.getProperty("user.home"), ".wavu/preferences.preferences_pb")
                .also { it.parentFile?.mkdirs() }
                .absolutePath
                .toPath()
        }
    )
}