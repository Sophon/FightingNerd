package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.util.stripMarkdownLinks
import io.github.sophon.discord.app.model.discord.AutocompleteChoice
import io.github.sophon.discord.app.model.discord.EncodedCharacter
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.discord.Command.Argument.AutoCompleteType
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.inPort.ProduceAutoCompleteUseCase

/**
 * flow: [flow-autocomplete.mmd](../../../docs/flow-autocomplete.mmd)
 *
 * logic: [flow-autocomplete.mmd](../../../docs/logic-autocomplete.mmd)
 */
internal class ProduceAutoCompleteService(
    private val characterService: CharacterService,
    private val moveService: MoveService,
): ProduceAutoCompleteUseCase {
    override suspend fun invoke(
        commandString: String,
        argument: String,
        query: String,
        argumentMap: Map<String, String>,
    ): List<AutocompleteChoice> {
        val command = Command.fromId(commandString) ?: return emptyList()
        val suggestions = routeFocusedType(
            command = command,
            argumentName = argument,
            query = query,
            argumentMap = argumentMap,
        )
        return suggestions
    }


    private suspend fun routeFocusedType(
        command: Command,
        argumentName: String,
        query: String,
        argumentMap: Map<String, String>,
    ): List<AutocompleteChoice> {
        val focusedType = command.argumentList
            .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
            ?.autoCompleteType
            ?: return emptyList()

        val choices = when (focusedType) {
            AutoCompleteType.Character -> getCharacterChoices(command, query)
            AutoCompleteType.Move -> getMoveChoices(command, query, argumentMap)
            AutoCompleteType.Other -> {
                when (command) {
                    Command.Alias -> getGameChoices(query)
                    Command.Stance -> getStanceChoices(command, query, argumentMap)
                    else -> emptyList()
                }
            }

            AutoCompleteType.None -> emptyList()
        }
        return choices
    }

    // /char only shows characters that have properties to display
    private suspend fun getCharacterChoices(command: Command, query: String): List<AutocompleteChoice> {
        val requireProperties = (command == Command.Char)
        val choiceList = characterService.getCharacters()
            .filter { requireProperties.not() || it.propertyList.isNotEmpty() }
            .filter { query.isBlank() || it.isApprox(query) }
            .map { it.toChoice() }
        return choiceList
    }

    private suspend fun getMoveChoices(
        command: Command,
        query: String,
        argumentMap: Map<String, String>,
    ): List<AutocompleteChoice> {
        val choiceList = getSiblingCharacterMoves(command, argumentMap)
            .filter { query.isBlank() || it.isApprox(query) }
            .map { it.toChoice() }
        return choiceList
    }

    private suspend fun getStanceChoices(
        command: Command,
        query: String,
        argumentMap: Map<String, String>,
    ): List<AutocompleteChoice> {
        val choiceList = getSiblingCharacterMoves(command, argumentMap)
            .mapNotNull { it.stance }
            .distinct()
            .filter { query.isBlank() || it.contains(query, ignoreCase = true) }
            .map { AutocompleteChoice(name = it, value = it) }
        return choiceList
    }

    // only games that actually have characters loaded
    private suspend fun getGameChoices(query: String): List<AutocompleteChoice> {
        val choiceList = characterService.getCharacters()
            .map { it.game }
            .distinct()
            .filter { it.displayName.contains(query, ignoreCase = true) }
            .map { AutocompleteChoice(name = it.displayName, value = it.id) }
        return choiceList
    }

    // the character argument was already chosen via autocomplete, its value is encoded
    private suspend fun getSiblingCharacterMoves(
        command: Command,
        argumentMap: Map<String, String>,
    ): List<MoveResponse> {
        val characterValue = command.readSibling(argumentMap, type = AutoCompleteType.Character)
        val choiceValue = EncodedCharacter.decode(characterValue) ?: return emptyList()

        val movesResult = characterService.findCharacter(choiceValue)
            .flatMap { character ->
                val characterId = CharacterId(game = character.game, characterId = character.id)
                moveService.getMoves(characterId)
            }
        val moveList = when (movesResult) {
            is Result.Success -> movesResult.data
            is Result.Error -> emptyList()
        }
        return moveList
    }


    private fun CharacterResponse.isApprox(query: String): Boolean {
        val normalizedQuery = query.normalizeForMatch()
        val isApprox = (id == normalizedQuery)
                || displayName.normalizeForMatch().contains(normalizedQuery)
                || aliasList.any { it.normalizeForMatch().contains(normalizedQuery) }
        return isApprox
    }

    private fun MoveResponse.isApprox(query: String): Boolean {
        val isApprox = input.contains(query, ignoreCase = true)
                || moveName.orEmpty().contains(query, ignoreCase = true)
                || aliasList.any { it.contains(query, ignoreCase = true) }
        return isApprox
    }

    private fun CharacterResponse.toChoice(): AutocompleteChoice {
        val choice = AutocompleteChoice(
            name = "$displayName (${game.displayName})",
            value = EncodedCharacter(characterId = id, gameId = game.id).encode(),
        )
        return choice
    }

    private fun String.normalizeForMatch(): String {
        val normalized = replace(" ", "").lowercase()
        return normalized
    }

    private fun MoveResponse.toChoice(): AutocompleteChoice {
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

    private fun Command.readSibling(
        argumentMap: Map<String, String>,
        type: AutoCompleteType,
    ): String {
        val siblingArg = argumentList.firstOrNull { it.autoCompleteType == type }
        val value = siblingArg?.let { argumentMap[it.name] }.orEmpty()
        return value
    }
}


private const val COLUMN_MAX_GAP_L = 15
private const val FILL_CHAR = '_'
private const val DISCORD_CHOICE_MAX_LENGTH = 100
private const val ELLIPSIS = "..."
private const val CHOICE_FIELD_COUNT = 4
