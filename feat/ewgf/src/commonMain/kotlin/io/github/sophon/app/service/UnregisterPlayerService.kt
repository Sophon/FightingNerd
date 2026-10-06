package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.DeletePlayerPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.inPort.UnregisterPlayerUseCase
import io.github.sophon.model.EwgfError

internal class UnregisterPlayerService(
    private val deletePlayerPort: DeletePlayerPort,
): UnregisterPlayerUseCase {
    override suspend fun invoke(discordId: String): EmptyResult<EwgfError> {
        val result = deletePlayerPort.delete(discordId)
            .mapError { error -> EwgfError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "unregistered: $discordId" } }
            .onError { error -> Napier.e(tag = TAG) { "$discordId: $error" } }
        return result
    }


    private companion object {
        const val TAG = "UnregisterPlayerService"
    }
}
