package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.util.extractFirstWord
import io.github.sophon.core.util.normalizeWhiteSpace
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.Tracker
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.util.removeTag
import io.github.sophon.domain.Source

internal class RouteCommandToFeatureUseCase(
    private val featureList: List<DiscordRegisteredFeature>,
    private val tracker: Tracker,
) {
    suspend fun invoke(
        source: Source,
        message: String
    ): Result<BotOutput, BotError> {
        val fullQuery = message
            .removeTag()
            .trim()
            .normalizeWhiteSpace()
            .takeIf { it.isNotBlank() }
            ?: return Result.Error(BotError.InvalidQuery(message))

        val firstWord = fullQuery.extractFirstWord()

        return if (firstWord.isCommand()) {
            val (commandString, query) = formatQuery(fullQuery)
            useExplicitCommands(source, commandString, query)
        } else {
            useDefaultCommands(source, fullQuery).mapError { BotError.InvalidCommand(firstWord) }
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

    private fun String.isCommand(): Boolean {
        return featureList.any { feature ->
            val isOtherCommand = feature.otherCommands.any {
                it.name.equals(this, ignoreCase = true)
            }
            val isDefaultCommand = feature.defaultCommand?.name
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
                command = fullQuery.take(firstIndexOfSpace).trim()
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
                    it.name.equals(commandString, ignoreCase = true)
                }

            val commandToUse = explicitCommand ?: run {
                val defaultCommand = feature.defaultCommand ?: continue //feature has no default command, next
                if (defaultCommand.name.equals(commandString, ignoreCase = true)) {
                    defaultCommand
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
                is Result.Success -> {
                    tracker.recordSuccessfulCommand(
                        featureName = feature.featureInfo.name,
                        command = commandToUse,
                    )

                    result
                }
                is Result.Error -> {
                    if (result.error is BotError.UnknownMove) {
                        tracker.recordFailure()
                        result
                    }
                    else continue
                }
            }
        }

        tracker.recordFailure()
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
                command = defaultCommand,
                query = fullQuery,
            )

            return when (result) {
                is Result.Success -> {
                    tracker.recordSuccessfulCommand(
                        featureName = feature.featureInfo.name,
                        command = defaultCommand,
                    )

                    result
                }
                is Result.Error -> {
                    if (result.error is BotError.UnknownMove) {
                        tracker.recordFailure()
                        result
                    }
                    else continue
                }
            }
        }

        tracker.recordFailure()
        return Result.Error(BotError.InvalidQuery(fullQuery))
    }
}
