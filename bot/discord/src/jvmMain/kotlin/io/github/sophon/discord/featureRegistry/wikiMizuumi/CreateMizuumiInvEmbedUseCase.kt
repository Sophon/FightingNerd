package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_LONG_S
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.toButtons
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
        query: String,
    ): Result<BotOutput, BotError> {
        return wiki.fetchMoveList(query)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList
                    .filter { move ->
                        predicate(game, move)
                    }
                    .distinctBy { it.input }
            }.map { moveList ->
                val botOutput = BotOutput(
                    primaryEmbedBuilder = createMoveListEmbed(
                        featureInfo = featureInfo,
                        category = "${query.uppercase()} Inv",
                        moveList = moveList,
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = query),
                        duration = EMBED_BUTTON_DURATION_LONG_S.seconds,
                    ),
                )
                botOutput
            }
    }

    private fun predicate(game: Game, move: Move): Boolean {
        return when (game) {
            Game.MBTL -> {
                move.invulnerability.orEmpty().run {
                    isNotEmpty() && isReversal() && isFullyInv()
                } && move.input.isLastArc().not() && move.input.isShieldCounter().not()
            }
            Game.Uni2 -> {
                move.invulnerability.orEmpty().run {
                    isNotEmpty()
                }
            }
            Game.VSAV -> {
                move.invulnerability.orEmpty().run {
                    isNotEmpty() && isFullBodyInv()
                }
            }
            else -> false
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


    //region MB
    private fun String.isLastArc(): Boolean = this.contains("ABCD", ignoreCase = true)

    private fun String.isShieldCounter(): Boolean = this.startsWith("D~", ignoreCase = true)

    private fun String.isReversal(): Boolean = this.contains("1-")

    private fun String.isFullyInv(): Boolean = this.contains("Full", ignoreCase = true)
    //endregion

    private fun String.isFullBodyInv(): Boolean = this.contains("whole body", ignoreCase = true)


    private companion object {
        const val TEAL = 0x0007A9F5
    }
}