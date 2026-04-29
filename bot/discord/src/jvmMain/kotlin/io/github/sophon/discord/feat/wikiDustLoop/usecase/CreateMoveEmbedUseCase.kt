package io.github.sophon.discord.feat.wikiDustLoop.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.wikiDustLoop.moveDetailedEmbedBuilderBB
import io.github.sophon.discord.feat.wikiDustLoop.moveDetailedEmbedBuilderGG
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderBB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderDB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderGB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderGG

internal class CreateMoveEmbedUseCase {
    fun invoke(
        gameId: String,
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

        return when (Game.fromId(gameId)) {
            Game.GGST -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilderGG(move, featureInfo),
                        manualEditBuilder = moveDetailedEmbedBuilderGG(move, featureInfo)
                    ),
                    images = images,
                    buttons = BotOutput.ButtonSet(
                        buttonList = listOf(
                            BotOutput.EmbedButton(
                                label = "Details", action = BotOutput.EmbedButton.Action.Edit()
                            ),
                        )
                    ),
                )
            }
            Game.DBFZ -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilderDB(move, featureInfo),
                    images = images,
                )
            }
            Game.GBVSR -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilderGB(move, featureInfo),
                    images = images,
                )
            }
            Game.BBCF -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilderBB(move, featureInfo),
                        manualEditBuilder = moveDetailedEmbedBuilderBB(move, featureInfo)
                    ),
                    images = images,
                    buttons = BotOutput.ButtonSet(
                        buttonList = listOf(
                            BotOutput.EmbedButton(
                                label = "Details", action = BotOutput.EmbedButton.Action.Edit()
                            ),
                        )
                    ),
                )
            }
            else -> BotOutput(primaryEmbedBuilder = {})
        }
    }
}