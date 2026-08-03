package io.github.sophon.discord.feat.wikiDustLoop.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.wikiDustLoop.detailedMoveEmbedBuilder
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilder

@ExcludeFromCoverage("UI")
internal class CreateMoveEmbedUseCase {
    fun invoke(
        game: Game,
        character: Character,
        move: Move,
        featureInfo: FeatureInfo,
    ): BotOutput {
        val images = move.urls.hitboxImageList
            .takeIf { it.size >= 2 }
            ?.let {
                BotOutput.Images(
                    title = move.input,
                    titleUrl = move.urls.wikiUrl,
                    urls = it,
                )
            }

        return when (game) {
            Game.GGST, Game.BBCF, Game.MTFS -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilder(game, character, move, featureInfo),
                        manualEditBuilder = detailedMoveEmbedBuilder(game, character, move, featureInfo)
                    ),
                    images = images,
                    buttons = BotOutput.ButtonSet(
                        buttonList = listOf(
                            BotOutput.EmbedButton(
                                label = "Details", action = BotOutput.EmbedButton.Action.Edit,
                            ),
                        )
                    ),
                )
            }
            Game.DBFZ -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilder(game, character, move, featureInfo),
                    images = images,
                )
            }
            Game.GBVSR -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilder(game, character, move, featureInfo),
                    images = images,
                )
            }
            else -> BotOutput(primaryEmbedBuilder = {})
        }
    }
}