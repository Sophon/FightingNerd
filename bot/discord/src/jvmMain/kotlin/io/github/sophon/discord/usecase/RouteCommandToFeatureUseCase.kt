package io.github.sophon.discord.usecase

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.Command
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

        val (commandString, query) = parseFullQuery(fullQuery)

        return tryOnAllFeatures(commandString, query)
    }

    suspend fun invoke(
        commandString: String,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return tryOnAllFeatures(commandString, query)
    }

    private fun parseFullQuery(fullQuery: String): Pair<String, String> {
        return when (val firstIndexOfSpace = fullQuery.indexOf(' ')) {
            -1 -> {
                val command = fullQuery.trim()
                val query = ""
                command to query
            }
            else -> {
                val command = fullQuery.substring(0, firstIndexOfSpace).trim()
                val query = fullQuery.substring(firstIndexOfSpace + 1).trim()
                command to query
            }
        }
    }

    private suspend fun tryOnAllFeatures(
        commandString: String,
        query: String
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        for (feature in featureList) {
            val (command, isDefault) = getFeatureCommand(feature, commandString)

            val result = feature.execute(command, query)

            when {
                result is Result.Success -> return result
                isDefault.not() -> return result
            }
        }

        return Result.Error(BotError.INVALID_QUERY)
    }

    private fun getFeatureCommand(
        feature: DiscordRegisteredFeature,
        commandString: String
    ): Pair<Command, Boolean> {
        val explicitCommand = feature.otherCommands
            .firstOrNull {
                it.command.name.equals(commandString, ignoreCase = true)
            }
            ?.command

        return if (explicitCommand == null) {
            feature.defaultCommand.command to true
        } else {
            explicitCommand to false
        }
    }
}