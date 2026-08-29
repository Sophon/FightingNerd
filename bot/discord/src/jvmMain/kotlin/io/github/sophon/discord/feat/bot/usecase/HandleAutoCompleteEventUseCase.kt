package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.filterMatching
import io.github.sophon.discord.AUTOCOMPLETE_VALUE_DELIMITER
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.feat.bot.model.AutocompleteChoice
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.Command.Argument.AutoCompleteType
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature

@ExcludeFromCoverage("UI")
internal class HandleAutoCompleteEventUseCase(
    botFeatureRepo: BotFeatureRepo,
) {
    private val featureList by lazy {
        botFeatureRepo.getFeatures()
    }

    suspend fun invoke(interaction: AutoCompleteInteraction) {
        val commandString = interaction.command.rootName.lowercase()
        val focusedArgumentName = interaction.command.options.entries
            .firstOrNull { it.value.focused }
            ?.key
            .orEmpty()
        val query = interaction.focusedOption.value

        val suggestions = routeToFeature(
            commandString = commandString,
            argumentName = focusedArgumentName,
            query = query,
            interaction = interaction,
        )
        interaction.suggestString {
            suggestions.forEach { choice(it.name, it.value) }
        }
    }

    private suspend fun routeToFeature(
        commandString: String,
        argumentName: String,
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val trimmedQuery = query.trim()

        val choices = if (commandString.equals(Command.Fd.name, ignoreCase = true)) {
            routeGlobalFd(
                argumentName = argumentName,
                query = trimmedQuery,
                interaction = interaction,
            )
        } else {
            routeGameSpecificFd(
                commandString = commandString,
                argumentName = argumentName,
                query = trimmedQuery,
                interaction = interaction,
            )
        }
        return choices
    }

    private suspend fun routeGlobalFd(
        argumentName: String,
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val focusedType = Command.Fd.argumentList
            .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
            ?.autoCompleteType
            ?: return emptyList()

        val choices = when (focusedType) {
            AutoCompleteType.Character -> globalFdCharacterChoices(query)
            AutoCompleteType.Move -> globalFdMoveChoices(query, interaction)
            AutoCompleteType.None -> emptyList()
        }
        return choices
    }

    private suspend fun globalFdCharacterChoices(query: String): List<AutocompleteChoice> {
        val gameCharacterList = mutableListOf<Triple<DiscordRegisteredFeature, Game, Character>>()
        for (feature in featureList) {
            val wikiFeature = (feature as? GameWikiDiscordFeature) ?: continue
            val result = wikiFeature.getAllCharacters()
            if (result is Result.Success) {
                val pair = result.data.map { (game, character) ->
                    Triple(feature, game, character)
                }
                gameCharacterList += pair
            }
        }
        val filtered = if (query.isEmpty()) {
            gameCharacterList
        } else {
            gameCharacterList.filter { (_, _, character) ->
                character.displayName.contains(query, ignoreCase = true)
                        || character.aliasList.any { it.equals(query, ignoreCase = true) }
            }
        }
        val choices = filtered
            .take(COMMAND_MAX_SUGGESTIONS)
            .map { (feature, game, character) ->
                AutocompleteChoice(
                    name = "${character.displayName} (${game.displayName})",
                    value = "${character.id}$AUTOCOMPLETE_VALUE_DELIMITER${feature.featureInfo.name}",
                )
            }
        return choices
    }

    private suspend fun globalFdMoveChoices(
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val characterValue = Command.Fd.readSibling(interaction, AutoCompleteType.Character)
        if (characterValue.isBlank()) return emptyList()

        val parts = characterValue.split(AUTOCOMPLETE_VALUE_DELIMITER, limit = 2)
        if (parts.size == 2) {
            val (characterId, featureTag) = parts
            val feature = featureList.firstOrNull { it.featureInfo.name == featureTag }
                ?: return emptyList()
            val wikiFeature = (feature as? GameWikiDiscordFeature) ?: return emptyList()
            val result = wikiFeature.getMoveList(Command.Fd, characterId)
            val moves = (result as? Result.Success)?.data.orEmpty()
            val filtered = moves.filterMovesByQuery(query)
            return filtered
        }

        // fallback for manually-typed character values without the encoded feature tag
        for (feature in featureList) {
            val wikiFeature = (feature as? GameWikiDiscordFeature) ?: continue
            val result = wikiFeature.getMoveList(Command.Fd, characterValue)
            if (result is Result.Success && result.data.isNotEmpty()) {
                val filtered = result.data.filterMovesByQuery(query)
                return filtered
            }
        }
        return emptyList()
    }

    private suspend fun routeGameSpecificFd(
        commandString: String,
        argumentName: String,
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        for (feature in featureList) {
            val wikiFeature = (feature as? GameWikiDiscordFeature) ?: continue

            val command = feature.otherCommands
                .firstOrNull { it.name.equals(commandString, ignoreCase = true) }
                ?: (feature.defaultCommand?.takeIf { it.name.equals(commandString, ignoreCase = true) })
                ?: continue

            val focusedArg = command.argumentList
                .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
                ?: continue

            when (focusedArg.autoCompleteType) {
                AutoCompleteType.Character -> {
                    wikiFeature.getCharacterList(command).map { characterList ->
                        val filtered = if (query.isEmpty()) {
                            characterList.map { it.toChoice() }
                        } else {
                            characterList.filterByQuery(query)
                        }
                        return filtered.take(COMMAND_MAX_SUGGESTIONS)
                    }
                }
                AutoCompleteType.Move -> {
                    val characterValue = command.readSibling(interaction, AutoCompleteType.Character)
                    if (characterValue.isBlank()) return emptyList()

                    val result = wikiFeature.getMoveList(command, characterValue)
                    if (result is Result.Success) {
                        val filtered = result.data.filterMovesByQuery(query)
                        return filtered
                    }
                }
                AutoCompleteType.None -> return emptyList()
            }
        }
        return emptyList()
    }

    private fun List<Character>.filterByQuery(query: String): List<AutocompleteChoice> {
        if (query.isEmpty()) {
            return this
                .takeIf { it.size <= COMMAND_MAX_SUGGESTIONS }
                ?.map { it.toChoice() }
                ?: emptyList()
        }

        val matchingCharList = this.filter { character ->
            character.displayName.contains(query, ignoreCase = true)
                    || character.aliasList.any { it.equals(query, ignoreCase = true) }
        }
        val choiceList = matchingCharList
            .map { it.toChoice() }
            .take(COMMAND_MAX_SUGGESTIONS)
        return choiceList
    }

    private fun List<Move>.filterMovesByQuery(query: String): List<AutocompleteChoice> {
        if (query.isEmpty()) {
            return this
                .take(COMMAND_MAX_SUGGESTIONS)
                .map { it.toChoice() }
        }

        val matchingMoveList = this.filterMatching(query)
        val choiceList = matchingMoveList
            .map { it.toChoice() }
            .take(COMMAND_MAX_SUGGESTIONS)
        return choiceList
    }

    private fun Command.readSibling(
        interaction: AutoCompleteInteraction,
        type: AutoCompleteType,
    ): String {
        val siblingArg = argumentList.firstOrNull { it.autoCompleteType == type }
        val value = siblingArg?.let { interaction.command.strings[it.name] }.orEmpty()
        return value
    }

    private fun Character.toChoice(): AutocompleteChoice {
        return AutocompleteChoice(name = displayName, value = id)
    }

    private fun Move.toChoice(): AutocompleteChoice {
        return AutocompleteChoice(name = input, value = input)
    }
}
