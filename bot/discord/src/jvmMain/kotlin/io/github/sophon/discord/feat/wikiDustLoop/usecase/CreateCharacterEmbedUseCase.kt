package io.github.sophon.discord.feat.wikiDustLoop.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.wikiDustLoop.charEmbedBuilder

@ExcludeFromCoverage("UI")
internal class CreateCharacterEmbedUseCase {
    operator fun invoke(
        game: Game,
        character: Character,
        fastestMoveList: List<Move>,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        val builder = charEmbedBuilder(
            game = game,
            character = character,
            fastestMoveList = fastestMoveList,
            featureInfo = featureInfo,
        )

        builder.invoke(this)
    }
}
