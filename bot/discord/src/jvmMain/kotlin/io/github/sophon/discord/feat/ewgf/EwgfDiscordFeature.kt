package io.github.sophon.discord.feat.ewgf

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.ewgf.usecase.GetRecentMatchesUseCase
import io.github.sophon.discord.feat.ewgf.usecase.ParseQueryIntoOperationUseCase
import io.github.sophon.discord.feat.ewgf.usecase.RegisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UnregisterPlayerUseCase
import io.github.sophon.discord.feat.ewgf.usecase.UpdatePlayerUseCase
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.integration.model.Source
import io.github.sophon.domain.model.Player

internal class EwgfDiscordFeature(
    ewgfFeatureInfo: EwgfFeatureInfo,
    private val parseQueryIntoOperationUseCase: ParseQueryIntoOperationUseCase,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val getPlayerUseCase: GetRecentMatchesUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase,
    private val unregisterPlayerUseCase: UnregisterPlayerUseCase,
): DiscordRegisteredFeature {
    override val featureInfo: FeatureInfo = ewgfFeatureInfo.featureInfo
    override val defaultCommand = Command.Ewgf
    override val otherCommands = emptyList<Command>()

    override suspend fun start() {}

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        if (command != Command.Ewgf) {
            Result.Error(BotError.BotLogicError(command.name, query))
        }

        return parseQueryIntoOperationUseCase.invoke(query)
            .flatMap { operation ->
                performOperation(
                    discordId = origin.id,
                    operation = operation,
                )
            }
    }

    private suspend fun performOperation(
        discordId: String,
        operation: EwgfOperations.Operation,
    ): Result<BotOutput, BotError> {
        val result = when (operation) {
            is EwgfOperations.Operation.Help -> {
                Result.Success(ewgfHelpEmbed(featureInfo))
            }
            is EwgfOperations.Operation.Register -> {
                registerPlayerUseCase.invoke(
                    discordId = discordId,
                    polarisId = operation.polarisId,
                ).map { successEmbed(operation, featureInfo) }
            }
            is EwgfOperations.Operation.Data -> {
                getPlayerUseCase.invoke(discordId).map { data ->
                    recentSetsEmbed(data, featureInfo)
                }
            }
            is EwgfOperations.Operation.Update -> {
                updatePlayerUseCase.invoke(
                    player = Player(
                        discordId = discordId,
                        polarisId = operation.polarisId,
                    )
                ).map { successEmbed(operation, featureInfo) }
            }
            is EwgfOperations.Operation.Unregister -> {
                unregisterPlayerUseCase.invoke(discordId)
                    .map { successEmbed(operation, featureInfo) }
            }
            is EwgfOperations.Operation.Search -> {
                getPlayerUseCase.invoke(operation.discordId).map { data ->
                    recentSetsEmbed(data, featureInfo)
                }
            }
        }

        return when (result) {
            is Result.Success -> {
                val output = BotOutput(primaryEmbedBuilder = result.data)
                Result.Success(output)
            }
            is Result.Error -> {
                Result.Error(result.error)
            }
        }
    }
}
