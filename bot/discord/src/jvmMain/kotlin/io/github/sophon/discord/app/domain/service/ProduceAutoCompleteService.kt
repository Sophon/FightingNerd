package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.stripMarkdownLinks
import io.github.sophon.discord.AUTOCOMPLETE_VALUE_DELIMITER
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.app.domain.model.AutocompleteChoice
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.port.inbound.ProduceAutoCompleteUseCase

/**
 * flow: [flow_autocomplete.mmd](../../../docs/flow_autocomplete.mmd)
 */
internal class ProduceAutoCompleteService(
    private val characterService: CharacterService,
    private val moveService: MoveService,
): ProduceAutoCompleteUseCase {
    override suspend fun invoke(
        commandString: String,
        argument: String,
        query: String,
    ): List<AutocompleteChoice> {
        val command = Command.fromId(commandString)

        val suggestions: List<AutocompleteChoice> = when (command) {
            Command.Char -> getCharacterChoices(query)

            else -> emptyList()
        }

        return suggestions
    }


    private suspend fun getCharacterChoices(query: String): List<AutocompleteChoice> {
        val choiceList = characterService.getCharacters()
            .filter { query.isBlank() || it.isApprox(query) }
            .map { it.toChoice() }
        return choiceList
    }

    private fun BotResponse.CharacterResponse.isApprox(query: String): Boolean {
        val normalizedQuery = query.normalizeForMatch()
        val isApprox = (id == normalizedQuery)
                || displayName.normalizeForMatch().contains(normalizedQuery)
                || aliasList.any { it.normalizeForMatch().contains(normalizedQuery) }
        return isApprox
    }

    private fun BotResponse.CharacterResponse.toChoice(): AutocompleteChoice {
        val choice = AutocompleteChoice(
            name = "$displayName (${game.displayName})",
            value = "$id$AUTOCOMPLETE_VALUE_DELIMITER${game.name}",
        )
        return choice
    }

    private fun String.normalizeForMatch(): String {
        val normalized = replace(" ", "").lowercase()
        return normalized
    }

    private fun BotResponse.MoveResponse.toChoice(): AutocompleteChoice {
        val frameData = primaryFields
            .take(CHOICE_FIELD_COUNT)
            .joinToString(" | ") { it.value.formatForAutoComplete() }

        val rawName = buildString {
            append("$input ".padEnd(COLUMN_MAX_GAP_L, FILL_CHAR))
            append(" [ $frameData ] ")

            if (moveName.isNullOrBlank().not()) {
                repeat(5) { append("-") }
                append(" $moveName")
            }
        }
        val truncatedName = rawName.truncateForDiscord()
        val truncatedValue = input.truncateForDiscord()
        val choice = AutocompleteChoice(name = truncatedName, value = truncatedValue)
        return choice
    }

    private fun String.truncateForDiscord(): String {
        if (length <= DISCORD_CHOICE_MAX_LENGTH) return this
        val truncated = take(DISCORD_CHOICE_MAX_LENGTH - ELLIPSIS.length) + ELLIPSIS
        return truncated
    }

    private fun String?.formatForAutoComplete(): String {
        val cleaned = this
            ?.stripMarkdownLinks()
            ?.substringBefore("(")
            ?.substringBefore("~")
            ?.trim().orEmpty()
        val result = cleaned.ifBlank { "-" }
        return result
    }
}


//data received after having chosen a character
private data class DecodedCharacterValue(
    val characterId: String,
    val game: Game,
)


private const val COLUMN_MAX_GAP_L = 15
private const val FILL_CHAR = '_'
private const val DISCORD_CHOICE_MAX_LENGTH = 100
private const val ELLIPSIS = "..."
private const val CHOICE_FIELD_COUNT = 4
