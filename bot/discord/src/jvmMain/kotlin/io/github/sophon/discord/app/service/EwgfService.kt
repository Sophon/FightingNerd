package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.EwgfOperation
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.outPort.EwgfPort
import io.github.sophon.discord.app.util.toEwgfOperation

internal interface EwgfService {
    suspend fun performOperation(
        query: String,
        source: UserRequest.Source?,
    ): Result<EwgfResponse, BotError>
}

internal class EwgfServiceImpl(
    private val ewgfPort: EwgfPort,
): EwgfService {
    /**
     * Button paths have no [source] to act as the player.
     */
    override suspend fun performOperation(
        query: String,
        source: UserRequest.Source?,
    ): Result<EwgfResponse, BotError> {
        val result = if (source == null) {
            Result.Error(BotError.BotLogicError(Command.Ewgf.name, query))
        } else {
            query.toEwgfOperation()
                .flatMap { operation -> performOperation(operation = operation, discordId = source.id) }
        }
        return result
    }


    private suspend fun performOperation(
        operation: EwgfOperation,
        discordId: String,
    ): Result<EwgfResponse, BotError> {
        val result = when (operation) {
            is EwgfOperation.Help -> Result.Success(EwgfResponse.Help(dataSource = ewgfPort.dataSource))
            is EwgfOperation.Data -> ewgfPort.getRecentSets(discordId)
            is EwgfOperation.Search -> ewgfPort.getRecentSets(operation.discordId)
            is EwgfOperation.Register -> {
                ewgfPort.register(discordId = discordId, polarisId = operation.polarisId)
                    .map { createSuccess(operation) }
            }
            is EwgfOperation.Update -> {
                ewgfPort.updatePolarisId(discordId = discordId, polarisId = operation.polarisId)
                    .map { createSuccess(operation) }
            }
            is EwgfOperation.Unregister -> {
                ewgfPort.unregister(discordId)
                    .map { createSuccess(operation) }
            }
        }
        return result
    }

    private fun createSuccess(operation: EwgfOperation): EwgfResponse.Success {
        val success = EwgfResponse.Success(
            dataSource = ewgfPort.dataSource,
            operation = operation,
        )
        return success
    }
}
