package io.github.sophon.discord.feat.wikiMizuumi

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikimizuumi.integration.model.MizuumiFilter
import kotlin.time.Duration.Companion.seconds

internal class CreateMizuumiInvEmbedUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        featureInfo: FeatureInfo,
        charName: String,
    ): Result<BotOutput, BotError> {
        val filter = when (game) {
            Game.MBTL -> MizuumiFilter.MBTLInvincible
            Game.Uni2 -> MizuumiFilter.Uni2Invincible
            Game.VSAV -> MizuumiFilter.VSAVInvincible
            else -> Filter.None
        }

        return wiki.fetchMoveList(charName, filter)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList.distinctBy { it.input }
            }.map { moveList ->
                val botOutput = BotOutput(
                    primaryEmbedBuilder = mizuumiMoveListEmbed(
                        featureInfo = featureInfo,
                        category = "${charName.uppercase()} Inv",
                        moveList = moveList,
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = charName),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
                botOutput
            }
    }
}