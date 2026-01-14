package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikimizuumi.domain.MizuumiFilter
import kotlin.time.Duration.Companion.seconds

/**
 * TODO:
 * - move this to the Mizuumi feature module
 * - have the WikiClient have the optional Filter enum for fetchMoveList(filter: Filter)
 */
internal class CreateMizuumiInvEmbedUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        featureInfo: FeatureInfo,
        charName: String,
    ): Result<BotOutput, BotError> {
        val filter = game.getFilter()

        return wiki.fetchMoveList(charName, filter)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList.distinctBy { it.input }
            }.map { moveList ->
                val botOutput = BotOutput(
                    primaryEmbedBuilder = createMoveListEmbed(
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

    private fun Game.getFilter(): Filter {
        return when (this) {
            Game.MBTL -> MizuumiFilter.MBTLInvincible
            Game.Uni2 -> MizuumiFilter.Uni2Invincible
            Game.VSAV -> MizuumiFilter.VSAVInvincible
            else -> Filter.None
        }
    }

    private fun createMoveListEmbed(
        featureInfo: FeatureInfo,
        category: String,
        moveList: List<Move>,
    ): EmbedBuilder.() -> Unit = {
        color = Color(TEAL)

        val text = moveList
            .mapIndexed { index, move ->
                "${index + 1}. **${move.input}** (${move.invulnerability})"
            }
            .joinToString("\n")

        text
            .chunkByNewLines(delimiter = "\n", maxLength = EMBED_MAX_LENGTH)
            .forEachIndexed { index, data ->
                mandatoryField(
                    name = if (index == 0) "$category moves" else "",
                    value = data,
                )
            }

        featureFooter(featureInfo)
    }


    private companion object {
        const val TEAL = 0x0007A9F5
    }
}