package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.PrepareStoragePort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.onError
import io.github.sophon.inPort.ConfigureStatsUseCase
import io.github.sophon.model.StatsError

internal class ConfigureStatsService(
    private val prepareStoragePort: PrepareStoragePort,
) : ConfigureStatsUseCase {
    override suspend fun invoke(): EmptyResult<StatsError> {
        val result = prepareStoragePort.prepare()
            .onError { error -> Napier.e(tag = TAG) { error.errors.joinToString() } }
        return result
    }


    private companion object {
        const val TAG = "ConfigureStatsService"
    }
}
