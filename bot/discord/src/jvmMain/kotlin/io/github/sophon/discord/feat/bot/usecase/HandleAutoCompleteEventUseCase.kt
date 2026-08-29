package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
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
        val command = Command.fromId(interaction.command.rootName)
        val focusedArgumentName = interaction.command.options.entries
            .firstOrNull { it.value.focused }
            ?.key
            .orEmpty()
        val query = interaction.focusedOption.value

        val suggestions = when (command) {
            Command.Fd -> {
                routeGlobalFd(
                    argumentName = focusedArgumentName,
                    query = query.trim(),
                    interaction = interaction,
                )
            }
            Command.Alias -> {
                routeAlias(
                    argumentName = focusedArgumentName,
                    query = query.trim(),
                )
            }
            Command.Char -> {
                characterChoices(query = query, requireData = true)
            }

            else -> emptyList()
        }
        interaction.suggestString {
            suggestions.forEach { choice(it.name, it.value) }
        }
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
            AutoCompleteType.Character -> characterChoices(query)
            AutoCompleteType.Move -> globalFdMoveChoices(query, interaction)
            AutoCompleteType.Game,
            AutoCompleteType.None -> emptyList()
        }
        return choices
    }

    private fun routeAlias(
        argumentName: String,
        query: String,
    ): List<AutocompleteChoice> {
        val focusedType = Command.Alias.argumentList
            .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
            ?.autoCompleteType
            ?: return emptyList()

        val choices = when (focusedType) {
            AutoCompleteType.Game -> gameChoices(query)
            AutoCompleteType.Character,
            AutoCompleteType.Move,
            AutoCompleteType.None -> emptyList()
        }
        return choices
    }

    private suspend fun characterChoices(
        query: String,
        requireData: Boolean = false,
    ): List<AutocompleteChoice> {
        val gameCharacterList = mutableListOf<Triple<GameWikiDiscordFeature, Game, Character>>()
        for (feature in featureList) {
            val wikiFeature = (feature as? GameWikiDiscordFeature) ?: continue

            if (requireData && (feature as? DiscordRegisteredFeature)?.otherCommands?.contains(Command.Char) != true) {
                //if we require char data, the game must include the Char command
                continue
            }

            val result = wikiFeature.getAllCharacters()
            if (result is Result.Success) {
                val triples = result.data.map { (game, character) ->
                    Triple(wikiFeature, game, character)
                }
                gameCharacterList += triples
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
                    value = encodeCharacterValue(
                        characterId = character.id,
                        featureName = (feature as DiscordRegisteredFeature).featureInfo.name,
                        game = game,
                    ),
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

        val decoded = decodeCharacterValue(characterValue) ?: return emptyList()
        val wikiFeature = featureList
            .firstOrNull { it.featureInfo.name == decoded.featureName } as? GameWikiDiscordFeature
            ?: return emptyList()
        val result = wikiFeature.getMoveList(decoded.game, decoded.characterId)
        val moves = (result as? Result.Success)?.data.orEmpty()
        val filtered = moves.filterMovesByQuery(query)
        return filtered
    }

    private fun List<Move>.filterMovesByQuery(query: String): List<AutocompleteChoice> {
        if (query.isEmpty()) {
            val choices = this
                .take(COMMAND_MAX_SUGGESTIONS)
                .map { it.toChoice() }
            return choices
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

    private fun Move.toChoice(): AutocompleteChoice {
        return AutocompleteChoice(name = input, value = input)
    }

    private fun encodeCharacterValue(
        characterId: String,
        featureName: String,
        game: Game,
    ): String {
        val encoded = "$characterId$AUTOCOMPLETE_VALUE_DELIMITER$featureName$AUTOCOMPLETE_VALUE_DELIMITER${game.name}"
        return encoded
    }

    private fun gameChoices(query: String): List<AutocompleteChoice> {
        val games = featureList
            .filterIsInstance<GameWikiDiscordFeature>()
            .flatMap { (it as DiscordRegisteredFeature).featureInfo.supportedGameSet }
            .distinct()
        val filtered = if (query.isEmpty()) {
            games
        } else {
            games.filter { it.displayName.contains(query, ignoreCase = true) }
        }
        val choices = filtered
            .take(COMMAND_MAX_SUGGESTIONS)
            .map { game ->
                AutocompleteChoice(name = game.displayName, value = game.name)
            }
        return choices
    }

    private fun decodeCharacterValue(value: String): DecodedCharacterValue? {
        val parts = value.split(AUTOCOMPLETE_VALUE_DELIMITER, limit = 3)
        if (parts.size != 3) return null
        val (characterId, featureName, gameName) = parts
        val game = Game.entries.firstOrNull { it.name == gameName } ?: return null
        val decoded = DecodedCharacterValue(
            characterId = characterId,
            featureName = featureName,
            game = game,
        )
        return decoded
    }

    private data class DecodedCharacterValue(
        val characterId: String,
        val featureName: String,
        val game: Game,
    )
}
