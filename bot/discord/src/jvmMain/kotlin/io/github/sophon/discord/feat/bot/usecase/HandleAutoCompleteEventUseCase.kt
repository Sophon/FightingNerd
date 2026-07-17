package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.feat.bot.model.AutocompleteChoice
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import kotlin.getValue

internal class HandleAutoCompleteEventUseCase(
    botFeatureRepo: BotFeatureRepo,
) {
    private val featureList by lazy {
        botFeatureRepo.getFeatures()
    }

    suspend fun invoke(interaction: AutoCompleteInteraction) {
        val commandString = interaction.command.rootName.lowercase()
        val query = interaction.focusedOption.value

        val suggestions = routeToFeature(commandString = commandString, query = query)
        interaction.suggestString {
            suggestions.forEach { choice(it.name, it.value) }
        }
    }

    private suspend fun routeToFeature(
        commandString: String,
        query: String,
    ): List<AutocompleteChoice> {
        for (feature in featureList) {
            val wikiFeature = feature as? GameWikiDiscordFeature ?: continue

            val command = feature.otherCommands
                .firstOrNull { it.name.equals(commandString, ignoreCase = true) }
                ?: (feature.defaultCommand?.takeIf { it.name.equals(commandString, ignoreCase = true) })
                ?: continue

            val result = wikiFeature.getCharacterList(command)
            if (result is Result.Success) {
                val filtered = result.data.filterByQuery(query)
                return filtered
            }
        }
        return emptyList()
    }

    private fun List<Character>.filterByQuery(query: String): List<AutocompleteChoice> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return this
                .takeIf { it.size <= COMMAND_MAX_SUGGESTIONS }
                ?.map { it.toChoice() }
                ?: emptyList()
        }
        val charsContaining = this.filter { it.displayName.contains(trimmed, ignoreCase = true) }
        val filteredChoices = charsContaining
            .map { it.toChoice() }
            .takeIf { it.size <= COMMAND_MAX_SUGGESTIONS }
            ?: emptyList()
        return filteredChoices
    }

    private fun Character.toChoice(): AutocompleteChoice = AutocompleteChoice(name = displayName, value = id)
}
