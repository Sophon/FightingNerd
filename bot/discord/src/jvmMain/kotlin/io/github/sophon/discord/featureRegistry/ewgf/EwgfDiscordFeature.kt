package io.github.sophon.discord.featureRegistry.ewgf

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.domain.model.Command
import io.github.sophon.discord.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.ewgf.usecase.GetPlayerDataUseCase
import io.github.sophon.discord.featureRegistry.ewgf.usecase.ParseQueryIntoOperationUseCase
import io.github.sophon.discord.featureRegistry.ewgf.usecase.RegisterPlayerUseCase
import io.github.sophon.discord.featureRegistry.ewgf.usecase.UnregisterPlayerUseCase
import io.github.sophon.discord.featureRegistry.ewgf.usecase.UpdatePlayerUseCase
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.Battle
import io.github.sophon.domain.EwgfFeatureInfo
import io.github.sophon.domain.Player
import io.github.sophon.domain.Source

internal class EwgfDiscordFeature(
    ewgfFeatureInfo: EwgfFeatureInfo,
    private val parseQueryIntoOperationUseCase: ParseQueryIntoOperationUseCase,
    private val registerPlayerUseCase: RegisterPlayerUseCase,
    private val getPlayerUseCase: GetPlayerDataUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase,
    private val unregisterPlayerUseCase: UnregisterPlayerUseCase,
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
                ).map { successEmbed(operation) }
            }
            is EwgfOperations.Operation.Data -> {
                getPlayerUseCase.invoke(discordId).map { data ->
                    dataEmbed(data)
                }
            }
            is EwgfOperations.Operation.Update -> {
                updatePlayerUseCase.invoke(
                    player = Player(
                        discordId = discordId,
                        polarisId = operation.polarisId,
                    )
                ).map { successEmbed(operation) }
            }
            is EwgfOperations.Operation.Unregister -> {
                unregisterPlayerUseCase.invoke(discordId)
                    .map { successEmbed(operation) }
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

    private fun dataEmbed(battleList: List<Battle>): EmbedBuilder.() -> Unit = {
        title = "EWGF: ${battleList.firstOrNull()?.player?.name}"
        color = Color(PINK)

        val recent = battleList
            .sortedByDescending { it.date }
            .take(20)

        val lines = recent.joinToString("\n") { battle ->
            val circle = when (battle.score.outcome) {
                Battle.Score.Outcome.WIN -> "🟢"
                Battle.Score.Outcome.LOSE -> "🔴"
                Battle.Score.Outcome.DRAW -> "🟡"
            }
            val score = "${battle.score.playerRounds}:${battle.score.opponentRounds}"
            "$circle ${battle.player.character} vs ${battle.opponent.name} (${battle.opponent.character}) → $score"
        }

        mandatoryField(
            name = "",
            value = lines,
            inline = false,
        )

        featureFooter(featureInfo)
    }

    private fun successEmbed(
        operation: EwgfOperations.Operation,
    ): EmbedBuilder.() -> Unit = {
        title = "Success"
        color = Color(PINK)

        mandatoryField(
            name = "",
            value = operation::class.simpleName,
        )

        featureFooter(featureInfo)
    }


    private companion object {
        const val PINK = 0x9F5FF7
    }
}