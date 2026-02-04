package io.github.sophon.discord.featureRegistry.ewgf

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.ewgf.usecase.ParseQueryIntoOperationUseCase
import io.github.sophon.discord.featureRegistry.ewgf.usecase.RegisterPlayerUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.domain.Source

internal class EwgfDiscordFeature(
    ewgfFeatureInfo: EwgfFeatureInfo,
    private val parseQueryIntoOperationUseCase: ParseQueryIntoOperationUseCase,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
): DiscordRegisteredFeature {
    override val featureInfo: FeatureInfo = ewgfFeatureInfo.featureInfo
    override val defaultCommand = Command.Ewgf
    override val otherCommands = listOf(
        Command.EwgfOperation,
    )

    override suspend fun start() {}

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        if (command != Command.Ewgf && command != Command.EwgfOperation) {
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
            is EwgfOperations.Operation.Register -> {
                registerPlayerUseCase.invoke(
                    discordId = discordId,
                    polarisId = operation.polarisId,
                )
                    .map { successEmbed() }
            }
            is EwgfOperations.Operation.Data -> TODO()
            is EwgfOperations.Operation.Update -> TODO()
            is EwgfOperations.Operation.Unregister -> TODO()
        }

        val embedBuilder = when (result) {
            is Result.Success -> successEmbed()
            is Result.Error -> errorEmbed()
        }

        val output = BotOutput(
            primaryEmbedBuilder = embedBuilder
        )
        return Result.Success(output)
    }

    private fun dataEmbed(
        result: Result<String, BotError>
    ): EmbedBuilder.() -> Unit = {
        title = "Title placeholder"
        color = Color(PINK)

        when (result) {
            is Result.Success -> {
                mandatoryField(
                    name = "Field",
                    value = result.data,
                    inline = false,
                )
            }
            is Result.Error -> {
                mandatoryField(
                    name = "Error",
                    value = "Error fetching data",
                    inline = false,
                )
            }
        }
    }

    private fun successEmbed(): EmbedBuilder.() -> Unit = {
        TODO()
    }

    private fun errorEmbed(): EmbedBuilder.() -> Unit = {
        TODO()
    }


    private companion object {
        const val PINK = 0x9F5FF7
    }
}