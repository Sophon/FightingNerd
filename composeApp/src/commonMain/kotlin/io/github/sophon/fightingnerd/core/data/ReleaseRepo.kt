package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.coroutines.flow.Flow

internal interface ReleaseRepo {
    suspend fun saveLastSeenVersion(version: String): EmptyResult<DataError.Local>
    fun getLastSeenVersion(): Flow<String?>
    fun getReleases(): Flow<List<Release>>
}
