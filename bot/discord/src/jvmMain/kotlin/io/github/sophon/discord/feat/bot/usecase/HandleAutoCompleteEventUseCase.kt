package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.feat.bot.model.AutocompleteChoice
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.Command.Argument.AutoCompleteType
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import kotlin.collections.emptyList
import kotlin.getValue

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

        for (feature in featureList) {
            val wikiFeature = feature as? GameWikiDiscordFeature ?: continue

            val command = feature.otherCommands
                .firstOrNull { it.name.equals(commandString, ignoreCase = true) }
                ?: (feature.defaultCommand?.takeIf { it.name.equals(commandString, ignoreCase = true) })
                ?: continue

            val focusedArg = command.argumentList
                .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
                ?: continue

            when (focusedArg.autoCompleteType) {
                AutoCompleteType.Character -> {
                    val result = wikiFeature.getCharacterList(command)
                    if (result is Result.Success) {
                        val filtered = result.data.filterByQuery(trimmedQuery)
                        return filtered
                    }
                }
                AutoCompleteType.Move -> {
                    val characterValue = command.readSibling(interaction, AutoCompleteType.Character)
                    if (characterValue.isBlank()) return emptyList()
                    val result = wikiFeature.getMoveList(command, characterValue)
                    if (result is Result.Success) {
                        val filtered = result.data.filterMovesByQuery(trimmedQuery)
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
        val matchingCharList = this.filter { it.displayName.contains(query, ignoreCase = true) }
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
        val matchingMoveList = this.filter { move ->
            move.input.contains(query, ignoreCase = true) ||
                move.name?.contains(query, ignoreCase = true) == true ||
                move.aliases.any { alias -> alias.contains(query, ignoreCase = true) }
        }
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

    private fun Character.toChoice(): AutocompleteChoice = AutocompleteChoice(name = displayName, value = id)

    private fun Move.toChoice(): AutocompleteChoice = AutocompleteChoice(name = input, value = input)
}
