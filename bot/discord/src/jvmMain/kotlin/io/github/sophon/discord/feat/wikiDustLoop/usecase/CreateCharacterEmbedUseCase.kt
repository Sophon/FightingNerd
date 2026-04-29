package io.github.sophon.discord.feat.wikiDustLoop.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.feat.wikiDustLoop.charEmbedBuilderBB
import io.github.sophon.discord.feat.wikiDustLoop.charEmbedBuilderDB
import io.github.sophon.discord.feat.wikiDustLoop.charEmbedBuilderGB
import io.github.sophon.discord.feat.wikiDustLoop.charEmbedBuilderGG

internal class CreateCharacterEmbedUseCase {
    fun invoke(
        gameId: String,
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        val builder = when (Game.fromId(gameId)) {
            Game.GGST -> charEmbedBuilderGG(character, fastestMoveList, featureInfo)
            Game.DBFZ -> charEmbedBuilderDB(character, fastestMoveList, featureInfo)
            Game.GBVSR -> charEmbedBuilderGB(character, fastestMoveList, featureInfo)
            Game.BBCF -> charEmbedBuilderBB(character, fastestMoveList, featureInfo)
            else -> null
        }

        builder?.invoke(this)
    }
}
