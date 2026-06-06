package io.github.sophon.discord.feat.wikiDustLoop.usecase

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.wikiDustLoop.moveDetailedEmbedBuilderBB
import io.github.sophon.discord.feat.wikiDustLoop.moveDetailedEmbedBuilderGG
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderBB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderDB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderGB
import io.github.sophon.discord.feat.wikiDustLoop.moveEmbedBuilderGG

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
            Game.GGST -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilderGG(character, move, featureInfo),
                        manualEditBuilder = moveDetailedEmbedBuilderGG(character, move, featureInfo)
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
                    primaryEmbedBuilder = moveEmbedBuilderDB(character, move, featureInfo),
                    images = images,
                )
            }
            Game.GBVSR -> {
                BotOutput(
                    primaryEmbedBuilder = moveEmbedBuilderGB(character, move, featureInfo),
                    images = images,
                )
            }
            Game.BBCF -> {
                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = moveEmbedBuilderBB(character, move, featureInfo),
                        manualEditBuilder = moveDetailedEmbedBuilderBB(character, move, featureInfo)
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