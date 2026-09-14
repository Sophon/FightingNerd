package io.github.sophon.fightingnerd.core.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.ReleaseRepo
import io.github.sophon.fightingnerd.feat.changelog.data.ChangelogRemoteSource
import io.github.sophon.fightingnerd.feat.changelog.data.toDomain
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class ReleaseRepoImpl(
    private val store: DataStore<Preferences>,
    private val remoteSource: ChangelogRemoteSource,
): ReleaseRepo {
    override suspend fun saveLastSeenVersion(version: String): EmptyResult<DataError.Local> {
        try {
            store.edit { prefs -> prefs[KEY_LAST_SEEN_VERSION] = version }
            return Result.Success(Unit)
        } catch (_: Exception) {
            return Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getLastSeenVersion(): Flow<String?> {
        val lastSeenFlow = store.data
            .catch { emit(emptyPreferences()) }
            .map { prefs -> prefs[KEY_LAST_SEEN_VERSION] }
        return lastSeenFlow
    }

    override fun getReleases(): Flow<List<Release>> {
        val releasesFlow = flow {
            val releases = when (val result = remoteSource.getReleaseNotes()) {
                is Result.Success -> result.data.toDomain()
                is Result.Error -> emptyList()
            }
            emit(releases)
        }
        return releasesFlow
    }
}


private val KEY_LAST_SEEN_VERSION = stringPreferencesKey("last_seen_version")
