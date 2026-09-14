package io.github.sophon.fightingnerd.core.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.ReviewPolicyRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

internal class ReviewPolicyRepoImpl(
    private val store: DataStore<Preferences>,
) : ReviewPolicyRepo {

    override fun getInstallationTimestamp(): Flow<Instant?> {
        val flow = store.data
            .catch { emit(emptyPreferences()) }
            .map { prefs -> prefs[KEY_INSTALLATION_TIMESTAMP]?.let(Instant::fromEpochMilliseconds) }
        return flow
    }

    override suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<DataError.Local> {
        try {
            store.edit { prefs -> prefs[KEY_INSTALLATION_TIMESTAMP] = timestamp.toEpochMilliseconds() }
            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }
}


private val KEY_INSTALLATION_TIMESTAMP = longPreferencesKey("installation_timestamp")
