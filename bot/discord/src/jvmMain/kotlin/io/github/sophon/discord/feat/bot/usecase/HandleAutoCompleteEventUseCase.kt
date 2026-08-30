package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.util.filterMatching
import io.github.sophon.core.wiki.util.isApprox
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
        val query = interaction.focusedOption.value.trim()

        val suggestions = when (command) {
            Command.Fd -> routeFocusedType(Command.Fd, focusedArgumentName, query, interaction)
            Command.Alias -> routeFocusedType(Command.Alias, focusedArgumentName, query, interaction)
            Command.Char -> getCharacterChoices(query, Command.Char)

            Command.Pc -> getCharacterChoices(query, Command.Pc)
            Command.Heat -> getCharacterChoices(query, Command.Heat)
            Command.Homing -> getCharacterChoices(query, Command.Homing)
            Command.Stance -> routeFocusedType(Command.Stance, focusedArgumentName, query, interaction)

            else -> emptyList()
        }.take(COMMAND_MAX_SUGGESTIONS)

        interaction.suggestString {
            suggestions.forEach { choice(it.name, it.value) }
        }
    }

    private suspend fun routeFocusedType(
        command: Command,
        argumentName: String,
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val focusedType = command.argumentList
            .firstOrNull { it.name.equals(argumentName, ignoreCase = true) }
            ?.autoCompleteType
            ?: return emptyList()

        val choices = when (focusedType) {
            AutoCompleteType.Character -> getCharacterChoices(query, command)
            AutoCompleteType.Move -> getMoveChoices(query, interaction)
            AutoCompleteType.Other -> {
                when (command) {
                    Command.Alias -> getGameChoices(command, query)
                    Command.Stance -> getStanceChoices(query, interaction)
                    else -> emptyList()
                }
            }
            AutoCompleteType.None -> emptyList()
        }
        return choices
    }

    private suspend fun getCharacterChoices(
        query: String,
        command: Command? = null,
    ): List<AutocompleteChoice> {
        val choices = featureList
            .filter {
                if (command == null) {
                    true
                }
                else {
                    (it.defaultCommand?.equals(command) == true) || it.otherCommands.contains(command)
                }
            }
            .filterIsInstance<GameWikiDiscordFeature>()
            .flatMap { gameFeature ->
                val pairs = when (val result = gameFeature.getAllCharacters()) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyList()
                }
                val featureName = (gameFeature as? DiscordRegisteredFeature)
                    ?.featureInfo?.name.orEmpty()
                val choices = pairs.toAutoCompleteChoices(
                    predicate = { (_, character) ->
                        if (query.isBlank()) true
                        else character.isApprox(query)
                    },
                    toName = { (game, character) ->
                        "${character.displayName} (${game.displayName})"
                    },
                    toValue = { (game, character) ->
                        character.encodeCharacterValue(
                            featureName = featureName,
                            game = game,
                        )
                    }
                )
                choices
            }
        return choices
    }

    private suspend fun getMoveChoices(
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val characterValue = Command.Fd.readSibling(interaction, AutoCompleteType.Character)
        if (characterValue.isBlank()) return emptyList()

        val decoded = decodeCharacterValue(characterValue) ?: return emptyList()
        val wikiFeature = featureList
            .firstOrNull { it.featureInfo.name == decoded.featureName } as? GameWikiDiscordFeature
            ?: return emptyList()
        val moves = when (val result = wikiFeature.getMoveList(decoded.game, decoded.characterId)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
        val filtered = moves.filterMovesByQuery(query)
        return filtered
    }

    private fun getGameChoices(
        command: Command,
        query: String,
    ): List<AutocompleteChoice> {
        val choices = featureList
            .filter { it.otherCommands.contains(command) }
            .filterIsInstance<GameWikiDiscordFeature>()
            .map { (it as DiscordRegisteredFeature).featureInfo.supportedGameSet }
            .flatMap { gameSet ->
                gameSet
                    .toList()
                    .toAutoCompleteChoices(
                        predicate = { it.displayName.contains(query, ignoreCase = true) },
                        toName = { it.displayName },
                        toValue = { it.id }
                    )
            }

        return choices
    }

    private suspend fun getStanceChoices(
        query: String,
        interaction: AutoCompleteInteraction,
    ): List<AutocompleteChoice> {
        val characterValue = Command.Stance.readSibling(interaction, AutoCompleteType.Character)
        if (characterValue.isBlank()) return emptyList()
        val decoded = decodeCharacterValue(characterValue) ?: return emptyList()
        val wikiFeature = featureList
            .firstOrNull { it.featureInfo.name == decoded.featureName } as? GameWikiDiscordFeature
            ?: return emptyList()

        val stances = when (
            val result = wikiFeature.getList(
                command = Command.Stance,
                characterId = decoded.characterId,
            )
        ) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
        val choices = stances.toAutoCompleteChoices(
            predicate = { if (query.isBlank()) true else it.contains(query, ignoreCase = true) },
            toName = { it },
            toValue = { it },
        )
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

    private fun Character.encodeCharacterValue(
        featureName: String,
        game: Game,
    ): String {
        val id = this.id
        val encoded = "$id$AUTOCOMPLETE_VALUE_DELIMITER$featureName$AUTOCOMPLETE_VALUE_DELIMITER${game.name}"
        return encoded
    }

    private fun <T>List<T>.toAutoCompleteChoices(
        predicate: (T) -> Boolean,
        toName: (T) -> String,
        toValue: (T) -> String,
    ): List<AutocompleteChoice> {
        val filtered = this
            .filter(predicate)
            .map {
                AutocompleteChoice(
                    name = toName(it),
                    value = toValue(it),
                )
            }
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
}

//data received after having chosen a character
private data class DecodedCharacterValue(
    val characterId: String,
    val featureName: String,
    val game: Game,
)
