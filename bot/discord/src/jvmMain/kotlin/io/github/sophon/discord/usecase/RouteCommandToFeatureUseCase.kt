package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.util.removeTag
import io.github.sophon.domain.Source

internal class RouteCommandToFeatureUseCase(
    private val featureList: List<DiscordRegisteredFeature>,
) {
    suspend fun invoke(
        source: Source,
        message: String
    ): Result<BotOutput, BotError> {
        val fullQuery = message
            .removeTag()
            .takeIf { it.isNotBlank() }
            ?: return Result.Error(BotError.InvalidQuery(message))

        val firstWord = extractFirstWord(fullQuery)

        return if (firstWord.isCommand()) {
            val (commandString, query) = formatQuery(fullQuery)
            useExplicitCommands(source, commandString, query)
        } else {
            useDefaultCommands(source, fullQuery)
        }
    }

    suspend fun invoke(
        commandString: String,
        source: Source,
        query: String,
    ): Result<BotOutput, BotError> {
        return useExplicitCommands(
            source,
            commandString,
            query,
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
        source: Source,
        commandString: String,
        query: String,
    ): Result<BotOutput, BotError> {
        var result: Result<BotOutput, BotError> = Result.Error(BotError.InvalidQuery(query))

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

            result = feature.execute(
                command = commandToUse,
                query = query,
                origin = source,
            )

            return when (result) {
                is Result.Success -> result
                is Result.Error -> {
                    if (result.error is BotError.UnknownMove) result
                    else continue
                }
            }
        }

        //use the last error
        return result
    }

    private suspend fun useDefaultCommands(
        source: Source,
        fullQuery: String
    ): Result<BotOutput, BotError> {
        for (feature in featureList) {
            val defaultCommand = feature.defaultCommand ?: continue

            val result = feature.execute(
                origin = source,
                command = defaultCommand.command,
                query = fullQuery,
            )

            return when (result) {
                is Result.Success -> result
                is Result.Error -> {
                    if (result.error is BotError.UnknownMove) result
                    else continue
                }
            }
        }

        return Result.Error(BotError.InvalidQuery(fullQuery))
    }
}