package io.github.sophon.discord.featureRegistry.wikiDustLoop.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveDetailedEmbedBuilderGG
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedBuilderBB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedBuilderDB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedBuilderGB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedBuilderGG

internal class CreateMoveEmbedUseCase {
    fun invoke(
        gameId: String,
        move: Move,
        featureInfo: FeatureInfo,
    ): BotOutput {
        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()

        return when (Game.fromId(gameId)) {
            Game.GGST -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilderGG(move, featureInfo),
                        manualEditBuilder = moveDetailedEmbedBuilderGG(move, featureInfo)
                    ),
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    },
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
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    }
                )
            }
            Game.GBVSR -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilderGB(move, featureInfo),
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    }
                )
            }
            Game.BBCF -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilderBB(move, featureInfo),
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    }
                )
            }
            else -> BotOutput(primaryEmbedBuilder = {})
        }
    }
}