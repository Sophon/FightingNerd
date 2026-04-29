package io.github.sophon.discord.feat.wikiWavu.usecase

import dev.kord.common.Color
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.domain.Filter
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.discord.feat.core.moveListEmbed
import io.github.sophon.discord.usecase.GetMovesUseCase
import io.github.sophon.discord.util.toButtons
import kotlin.time.Duration.Companion.seconds

//TODO: write unit tests
internal class GetStancesUseCase(
    private val getMovesUseCase: GetMovesUseCase,
) {
    suspend fun invoke(
        featureInfo: FeatureInfo,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        val charName: String
        val stance: String
        query.split(" ").let { queries ->
            charName = queries.firstOrNull() ?: ""
            stance = queries.drop(1).joinToString(" ").uppercase()
        }

        return if (stance.isBlank()) {
            fetchStances(wiki, charName).map { stanceList ->
                val buttons = mutableListOf<BotOutput.EmbedButton>()
                var text = ""

                stanceList.forEachIndexed { index, stance ->
                    val order = (index + 1).toString()
                    val query = "stance $charName $stance"
                    buttons.add(
                        BotOutput.EmbedButton(
                            label = order,
                            action = BotOutput.EmbedButton.Action.Query(query),
                        )
                    )
                    text += "$order. **${stance.uppercase()}**\n"
                }

                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = "${charName.uppercase()} stances",
                        dataList = stanceList,
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = buttons,
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
            }
        } else {
            fetchMovesOfStance(wiki, charName, stance).map { moveList ->
                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = stance.uppercase(),
                        dataList = moveList.map { it.input },
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = charName),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    )
                )
            }
        }
    }


    private suspend fun fetchMovesOfStance(
        wiki: WikiClient,
        charName: String,
        stance: String,
    ): Result<List<Move>, BotError> {
        val filter = object : Filter {
            override val predicate: (Move) -> Boolean = { move ->
                move.t8Properties?.stance.equals(stance, ignoreCase = true)
            }
        }

        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = charName,
            filter = filter,
        )
    }

    private suspend fun fetchStances(
        wiki: WikiClient,
        charName: String,
    ): Result<List<String>, BotError> {
        return wiki.fetchMoveList(charName)
            .mapError { it.toDomainError() }
            .map { moveList ->
                moveList
                    .filter { it.t8Properties?.stance?.isNotBlank() == true }
                    .map { it.t8Properties!!.stance!! }
                    .distinct()
            }
    }


    private companion object {
        private const val BLUE = 0x00095FB
    }
}