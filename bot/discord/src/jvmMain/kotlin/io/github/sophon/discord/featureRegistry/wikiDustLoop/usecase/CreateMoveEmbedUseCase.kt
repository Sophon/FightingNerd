package io.github.sophon.discord.featureRegistry.wikiDustLoop.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedBB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedDB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedGB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.moveEmbedGG

internal class CreateMoveEmbedUseCase {
    fun invoke(
        move: Move,
        game: Game,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        when (game) {
            Game.GGST -> moveEmbedGG(move, featureInfo)
            Game.DBFZ -> moveEmbedDB(move, featureInfo)
            Game.GBVSR -> moveEmbedGB(move, featureInfo)
            Game.BBCF -> moveEmbedBB(move, featureInfo)
            else -> {}
        }
    }
}