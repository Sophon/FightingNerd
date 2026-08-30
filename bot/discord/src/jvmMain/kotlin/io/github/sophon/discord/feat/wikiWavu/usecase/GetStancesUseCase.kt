package io.github.sophon.discord.feat.wikiWavu.usecase

import dev.kord.common.Color
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.ui.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikiwavu.integration.model.T8Properties
import kotlinx.coroutines.flow.first
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

        val botOutput = if (stance.isBlank()) {
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
            fetchMovesOfStance(wiki, charName, stance).map { (character, moveList) ->
                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = stance.uppercase(),
                        dataList = moveList.map { it.input },
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = character.id),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    )
                )
            }
        }
        return botOutput
    }

    suspend fun invoke(
        wiki: WikiClient,
        charName: String,
    ): Result<List<String>, BotError> {
        val result = fetchStances(wiki, charName)
        return result
    }


    private suspend fun fetchMovesOfStance(
        wiki: WikiClient,
        charName: String,
        stance: String,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val filter = object : Filter {
            override val name: String = "Stance"
            override val predicate: (Move) -> Boolean = { move ->
                (move.gameProperties as? T8Properties)?.stance.equals(stance, ignoreCase = true)
            }
        }

        return getMovesUseCase.invoke(
            wiki = wiki,
            characterQuery = charName,
            filter = filter,
        )
    }

    private suspend fun fetchStances(
        wiki: WikiClient,
        charName: String,
    ): Result<List<String>, BotError> {
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(charName)
            ?: return Result.Error(BotError.UnknownCharacter(charName))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
        val stanceList = moveList
            .filter { (it.gameProperties as? T8Properties)?.stance?.isNotBlank() == true }
            .map { (it.gameProperties as T8Properties).stance!! }
            .distinct()
        val result = Result.Success(stanceList)
        return result
    }


    private companion object {
        private const val BLUE = 0x00095FB
    }
}