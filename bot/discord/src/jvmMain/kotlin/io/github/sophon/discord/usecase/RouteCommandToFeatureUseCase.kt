package io.github.sophon.discord.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.util.removeTag

internal class RouteCommandToFeatureUseCase(
    private val featureList: List<DiscordRegisteredFeature>,
) {
    suspend fun invoke(
        message: String
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        val fullQuery = message
            .removeTag()
            .lowercase()
            .takeIf { it.isNotBlank() }
            ?: return Result.Error(BotError.INVALID_QUERY)

        val firstWord = extractFirstWord(fullQuery)

        return if (firstWord.isCommand()) {
            val (commandString, query) = formatQuery(fullQuery)
            useExplicitCommands(commandString, query)
        } else {
            useDefaultCommands(fullQuery)
        }
    }

    suspend fun invoke(
        commandString: String,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return useExplicitCommands(
            commandString = commandString.lowercase(),
            query = query
        )
    }

    private fun extractFirstWord(query: String): String {
        val firstIndexOfSpace = query.indexOf(' ')
        return when (firstIndexOfSpace) {
            -1 -> query.trim()
            else -> query.substring(0, firstIndexOfSpace).trim()
        }
    }

    private fun String.isCommand(): Boolean {
        return featureList.any { feature ->
            val isOtherCommand = feature.otherCommands.any {
                it.command.name.equals(this, ignoreCase = true)
            }
            val isDefaultCommand = feature.defaultCommand?.command?.name
                .equals(this, ignoreCase = true)

            isOtherCommand || isDefaultCommand
        }
    }

    private fun formatQuery(fullQuery: String): Pair<String, String> {
        val command: String
        val query: String
        when (val firstIndexOfSpace = fullQuery.indexOf(' ')) {
            -1 -> {
                command = fullQuery.trim()
                query = ""
            }
            else -> {
                command = fullQuery.substring(0, firstIndexOfSpace).trim()
                query = fullQuery.substring(firstIndexOfSpace + 1).trim()
            }
        }

        return command to query
    }

    private suspend fun useExplicitCommands(
        commandString: String,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        var result: Result<EmbedBuilder.() -> Unit, BotError> = Result.Error(BotError.INVALID_QUERY)

        for (feature in featureList) {
            val explicitCommand = feature.otherCommands
                .firstOrNull {
                    it.command.name.equals(commandString, ignoreCase = true)
                }
                ?.command

            val commandToUse = explicitCommand ?: run {
                val defaultCommand = feature.defaultCommand ?: continue //feature has no default command, next
                if (defaultCommand.command.name.equals(commandString, ignoreCase = true)) {
                    defaultCommand.command
                } else {
                    continue //feature doesn't have this command, next
                }
            }

            result = feature.execute(commandToUse, query)
            if (result is Result.Success) {
                return result
            }
        }

        //use the last error
        return result
    }

    private suspend fun useDefaultCommands(
        fullQuery: String
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        for (feature in featureList) {
            val defaultCommand = feature.defaultCommand ?: continue

            val result = feature.execute(
                command = defaultCommand.command,
                query = fullQuery,
            )

            if (result is Result.Success) {
                return result
            }
        }

        return Result.Error(BotError.INVALID_QUERY)
    }
}