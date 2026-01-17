package io.github.sophon.discord.featureRegistry.wikiDustLoop.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.featureRegistry.wikiDustLoop.charEmbedBB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.charEmbedDB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.charEmbedGB
import io.github.sophon.discord.featureRegistry.wikiDustLoop.charEmbedGG

internal class CreateCharacterEmbedUseCase {
    fun invoke(
        gameId: String,
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        when (Game.fromId(gameId)) {
            Game.GGST -> charEmbedGG(character, fastestMoveList, featureInfo)
            Game.DBFZ -> charEmbedDB(character, fastestMoveList, featureInfo)
            Game.GBVSR -> charEmbedGB(character, fastestMoveList, featureInfo)
            Game.BBCF -> charEmbedBB(character, fastestMoveList, featureInfo)
            else -> {}
        }
    }
}