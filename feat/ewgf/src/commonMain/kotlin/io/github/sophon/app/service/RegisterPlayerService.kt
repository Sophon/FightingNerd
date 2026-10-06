package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.SavePlayerPort
import io.github.sophon.app.util.toPolarisId
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.inPort.RegisterPlayerUseCase
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

internal class RegisterPlayerService(
    private val savePlayerPort: SavePlayerPort,
): RegisterPlayerUseCase {
    override suspend fun invoke(player: Player): EmptyResult<EwgfError> {
        val formattedPlayer = player.copy(polarisId = player.polarisId.toPolarisId())

        val result = savePlayerPort.save(formattedPlayer)
            .mapError { error -> EwgfError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "registered: $formattedPlayer" } }
            .onError { error -> Napier.e(tag = TAG) { "${player.discordId}: $error" } }
        return result
    }


    private companion object {
        const val TAG = "RegisterPlayerService"
    }
}
