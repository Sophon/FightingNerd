package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField

/**
 * TODO: should refactor for the Wiki to use Filter class
 */
internal class CreateDustLoopInvincibleMovesEmbedUseCase {
    suspend fun invoke(
        wiki: WikiClient,
        featureInfo: FeatureInfo,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return wiki.fetchCharacterList()
            .map { characterList ->
                val moveList = mutableListOf<Move>()

                characterList.forEach { character ->
                    val charName = character.id
                    getInvincibleMoves(charName, wiki)
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
        charName: String,
        wikiClient: WikiClient,
    ): Result<List<Move>, WikiError> {
        return wikiClient.fetchMoveList(charName)
            .map { moveList ->
                val filtered = moveList.filter { move ->
                    val isFullyFromFrameOne = move.invulnerability.orEmpty()
                        .split(",")
                        .any {
                            it.startsWith("1~") && it.endsWith("All", ignoreCase = true)
                        }
                    val isOverdrive = move.input.contains("a+b+c+d", ignoreCase = true)
                            || move.input.endsWith("od", ignoreCase = true)
                    val isCounterAssault = move.input.contains("6a+b", ignoreCase = true)
                    val costsMeter = move.bbProperties?.type.orEmpty().contains("super", ignoreCase = true)
                            || move.input.endsWith("od", ignoreCase = true)
                            || move.bbProperties?.type.orEmpty().contains("astral", ignoreCase = true)
                            || move.input.endsWith("special", ignoreCase = true)
                    val isJump = move.input.startsWith("j.") || move.input.startsWith("d.")
                    val attack = move.input.endsWith("attack")

                    isFullyFromFrameOne
                            && isCounterAssault.not() && isOverdrive.not()
                            && costsMeter.not()
                            && isJump.not() && attack.not()
                }
                filtered
            }
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