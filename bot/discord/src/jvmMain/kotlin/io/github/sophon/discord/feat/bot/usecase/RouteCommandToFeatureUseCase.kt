package io.github.sophon.discord.feat.bot.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.extractFirstWord
import io.github.sophon.core.util.normalizeWhiteSpace
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.Tracker
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.removeTag
import io.github.sophon.integration.model.Source

internal class RouteCommandToFeatureUseCase(
    botFeatureRepo: BotFeatureRepo,
    private val tracker: Tracker,
) {
    private val featureList by lazy {
        botFeatureRepo.getFeatures()
    }

    suspend operator fun invoke(
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

        val result = if (firstWord.isCommand()) {
            val (commandString, query) = formatQuery(fullQuery)
            useExplicitCommands(source, commandString, query)
        } else {
            val intermediaryResult = useDefaultCommands(source, fullQuery)
            if (intermediaryResult is Result.Error) {
                extractCommand(source, fullQuery, originalResult = intermediaryResult)
            } else {
                intermediaryResult
            }
        }
        return result
    }

    suspend operator fun invoke(
        commandString: String,
        source: Source,
        query: String,
        featureHint: String? = null,
        game: Game? = null,
    ): Result<BotOutput, BotError> {
        val result = useExplicitCommands(
            source = source,
            commandString = commandString,
            query = query,
            featureHint = featureHint,
            game = game,
        )
        return result
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
        featureHint: String? = null,
        game: Game? = null,
    ): Result<BotOutput, BotError> {
        var result: Result<BotOutput, BotError> = Result.Error(BotError.InvalidQuery(query))

        val relevantFeatures = if (featureHint != null) {
            featureList.filter { it.featureInfo.name == featureHint }
        } else {
            featureList
        }

        for (feature in relevantFeatures) {
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
                game = game,
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
        return Result.Error(BotError.InvalidCommand(fullQuery.split(" ").first()))
    }

    /**
     * Should ONLY be called if the invocation is TAG-based **AND** on error.
     *
     * Tries to extract a command from anywhere in the query, then does a final pass
     * via [useExplicitCommands] with that command and the remaining words as the query.
     */
    private suspend fun extractCommand(
        source: Source,
        fullQuery: String,
        originalResult: Result<BotOutput, BotError>,
    ): Result<BotOutput, BotError> {
        val words = fullQuery.split(' ')
        words.forEach { word ->
            if (word.isCommand()) {
                val reformulatedQuery = (words - word).joinToString(" ")
                val result = useExplicitCommands(
                    source = source,
                    commandString = word,
                    query = reformulatedQuery,
                )
                return result
            }
        }

        return originalResult
    }
}
