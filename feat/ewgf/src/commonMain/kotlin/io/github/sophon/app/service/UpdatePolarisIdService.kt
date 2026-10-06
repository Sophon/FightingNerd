package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.UpdatePolarisIdPort
import io.github.sophon.app.util.toPolarisId
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.inPort.UpdatePolarisIdUseCase
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

internal class UpdatePolarisIdService(
    private val updatePolarisIdPort: UpdatePolarisIdPort,
): UpdatePolarisIdUseCase {
    override suspend fun invoke(player: Player): EmptyResult<EwgfError> {
        val polarisId = player.polarisId.toPolarisId()

        val result = updatePolarisIdPort.update(discordId = player.discordId, polarisId = polarisId)
            .mapError { error -> EwgfError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "updated: ${player.discordId} - $polarisId" } }
            .onError { error -> Napier.e(tag = TAG) { "${player.discordId}: $error" } }
        return result
    }


    private companion object {
        const val TAG = "UpdatePolarisIdService"
    }
}
