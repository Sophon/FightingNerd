package io.github.sophon.discord.feat.bot.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature

internal class RouteAutocompleteToFeatureUseCase(
    botFeatureRepo: BotFeatureRepo,
) {
    private val featureList by lazy {
        botFeatureRepo.getFeatures()
    }

    suspend fun invoke(
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

internal data class AutocompleteChoice(
    val name: String,
    val value: String,
)
