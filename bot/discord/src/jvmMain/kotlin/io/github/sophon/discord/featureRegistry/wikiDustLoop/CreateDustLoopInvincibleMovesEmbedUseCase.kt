package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.wikidustloop.domain.DustLoopFilter

/**
 * TODO: should refactor for the Wiki to use Filter class
 */
internal class CreateDustLoopInvincibleMovesEmbedUseCase {
    suspend fun invoke(
        game: Game,
        wiki: WikiClient,
        featureInfo: FeatureInfo,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return wiki.fetchCharacterList()
            .map { characterList ->
                val moveList = mutableListOf<Move>()

                characterList.forEach { character ->
                    val charName = character.id
                    getInvincibleMoves(game, charName, wiki)
                        .map { moveList += it }
                        .mapError {
                            it.toDomainError()
                        }
                }

                createInvincibleMovesEmbed(moveList, featureInfo)
            }
            .mapError { it.toDomainError() }
    }


    private suspend fun getInvincibleMoves(
        game: Game,
        charName: String,
        wiki: WikiClient,
    ): Result<List<Move>, WikiError> {
        val filter = when (game) {
            Game.BBCF -> DustLoopFilter.BBInvincible
            else -> Filter.None
        }

        return wiki.fetchMoveList(charName, filter)
    }

    private fun createInvincibleMovesEmbed(
        moveList: List<Move>,
        featureInfo: FeatureInfo,
    ): EmbedBuilder.() -> Unit = {
        color = Color(RED)

        val meterless = moveList
            .filter { it.startup != null }
            .groupBy { move ->
                move.startup!!.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
            }
            .toSortedMap()

        val text = meterless
            .map { (startup, moveList) ->
                buildString {
                    appendLine("**${startup}f**:")
                    moveList.groupBy { it.charName }
                        .forEach { (charName, moveList) ->
                            if (moveList.size == 1) {
                                appendLine("- **$charName** → ${moveList.first().input}")
                            } else {
                                appendLine("- **$charName**:")
                                moveList.forEach { move ->
                                    appendLine("   - ${move.input}")
                                }
                            }
                        }
                }
            }
            .joinToString("")

        text
            .chunkByNewLines(delimiter = "\n", maxLength = EMBED_MAX_LENGTH)
            .forEachIndexed { index, data ->
                mandatoryField(
                    name = if (index == 0) "Inv moves" else "",
                    value = data,
                )
            }

        featureFooter(featureInfo)
    }


    private companion object {
        const val RED = 0x00950117
    }
}