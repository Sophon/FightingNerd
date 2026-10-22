package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import kotlinx.coroutines.flow.Flow

internal interface LastSeenReleasePort {
    fun subscribeToLastSeenVersion(): Flow<String?>
    suspend fun saveLastSeenVersion(version: String): EmptyResult<AppError>
}
