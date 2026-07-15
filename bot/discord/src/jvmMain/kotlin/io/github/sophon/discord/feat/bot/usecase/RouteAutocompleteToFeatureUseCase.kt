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
        prefix: String,
    ): List<AutocompleteChoice> {
        for (feature in featureList) {
            val wikiFeature = feature as? GameWikiDiscordFeature ?: continue

            val command = feature.otherCommands
                .firstOrNull { it.name.equals(commandString, ignoreCase = true) }
                ?: (feature.defaultCommand?.takeIf { it.name.equals(commandString, ignoreCase = true) })
                ?: continue

            val result = wikiFeature.getCharacterList(command)
            if (result is Result.Success) {
                val filtered = result.data.filterByPrefix(prefix)
                return filtered
            }
        }
        return emptyList()
    }

    private fun List<Character>.filterByPrefix(prefix: String): List<AutocompleteChoice> {
        val trimmed = prefix.trim()
        if (trimmed.isEmpty()) {
            return this.take(COMMAND_MAX_SUGGESTIONS).map { it.toChoice() }
        }
        val startsWith = this.filter {
            it.displayName.startsWith(trimmed, ignoreCase = true)
        }
        val contains = this.filter {
            it.displayName.contains(trimmed, ignoreCase = true) &&
                it.displayName.startsWith(trimmed, ignoreCase = true).not()
        }
        val combined = (startsWith + contains)
            .map { it.toChoice() }
            .take(COMMAND_MAX_SUGGESTIONS)
        return combined
    }

    private fun Character.toChoice(): AutocompleteChoice = AutocompleteChoice(name = displayName, value = id)
}

internal data class AutocompleteChoice(
    val name: String,
    val value: String,
)
