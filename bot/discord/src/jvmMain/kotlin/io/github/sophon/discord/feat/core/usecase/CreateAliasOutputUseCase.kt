package io.github.sophon.discord.feat.core.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.ui.aliasEmbed
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.seconds

@ExcludeFromCoverage("UI")
internal class CreateAliasOutputUseCase(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
    featureRepo: FeatureRepo,
) {
    private val featureClientMap by lazy {
        featureRepo.getGameClients()
    }
    private val featureInfo = getBotFeatureInfoUseCase()

    suspend operator fun invoke(gameId: String?): Result<BotOutput, BotError> {
        if (gameId.isNullOrBlank())
            return Result.Success(promptForGameOutput())

        val botOutput = characterAliasOutput(gameId)
            ?: return Result.Error(BotError.UnsupportedGame(gameId))

        return Result.Success(botOutput)
    }


    private fun promptForGameOutput(): BotOutput {
        val embedBuilder: EmbedBuilder.() -> Unit = {
            color = Color(RED)
            mandatoryField(
                name = "Missing: Game ID",
                value = "Please select the game from the options below.",
            )

            featureFooter(featureInfo)
        }

        val embedButtons = BotOutput.ButtonSet(
            buttonList = featureClientMap
                .map { (game, _) ->
                    BotOutput.EmbedButton(
                        label = game.id,
                        action = BotOutput.EmbedButton.Action.Query(
                            query = "alias ${game.id}"
                        )
                    )
                },
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        )

        val output = BotOutput(
            primaryEmbedBuilder = embedBuilder,
            buttons = embedButtons,
        )

        return output
    }

    private suspend fun characterAliasOutput(
        gameId: String,
    ): BotOutput? {
        val wikiClient = featureClientMap[Game.fromId(gameId)]
            ?: return null

        val characterList = wikiClient
            .subscribeToCharacterList()
            .first()
        val embed = aliasEmbed(
            characterList = characterList,
            featureInfo = featureInfo,
            colorCode = RED,
        )

        val botOutput = BotOutput(primaryEmbedBuilder = embed)

        return botOutput
    }
}

private const val RED = 0x00950117
