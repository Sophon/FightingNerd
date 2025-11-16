package io.github.sophon.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.embed
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.discord.util.createErrorEmbed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface DiscordBot {
    suspend fun startSession()
}

internal class DiscordBotImpl(
    private val kord: Kord,
    private val featureList: List<DiscordRegisteredFeature>,
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
): DiscordBot {
    override suspend fun startSession() {
        startFeatures()
        startKord()
    }


    private suspend fun startFeatures() {
        supervisorScope {
            featureList.forEach { feature ->
                launch {
                    runCatching { feature.start() }
                        .onFailure {
                            Napier.e(tag = TAG) { "Failed to load ${feature.featureInfo.name}: $it" }
                        }
                }
            }
        }
    }

    private suspend fun startKord() {
        cleanOldGuildCommands(kord)
        createGlobalCommands()
//        createCommandsForTestServer()

        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            handleCommand()
        }

        kord.on<MessageCreateEvent> {
            // ignoring other bots, even ourselves. We only serve humans here!
            if (message.author?.isBot != false) return@on

            handleMessage()
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        kord.login {
            // we need to specify this to receive the content of messages
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    private suspend fun cleanOldGuildCommands(kord: Kord) {
        val testGuildSnowFlake = Snowflake(TEST_SERVER_ID)
        kord.getGuildApplicationCommands(testGuildSnowFlake).collect { command ->
            command.delete()
        }
    }

    private suspend fun MessageCreateEvent.handleMessage() {
        if (kord.selfId !in message.mentionedUserIds) return

        val result = routeCommandToFeatureUseCase.invoke(message.content)

        val embedBuilder = when(result) {
            is Result.Success -> result.data
            is Result.Error -> createErrorEmbed(result.error)
        }

        message.channel.createMessage {
            messageReference = message.id
            allowedMentions { repliedUser = false }
            embed(embedBuilder)
        }
    }

    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        val commandString = interaction.command.rootName
        val query = interaction.command.strings.values.joinToString(" ")

        val result = routeCommandToFeatureUseCase.invoke(commandString, query)

        val embedBuilder = when (result) {
            is Result.Success -> result.data
            is Result.Error -> createErrorEmbed(result.error)
        }

        interaction.respondPublic { embed(embedBuilder) }
    }

    private suspend fun createCommandsForTestServer() {
        val testGuildSnowFlake = Snowflake(TEST_SERVER_ID)
        kord.createGuildApplicationCommands(testGuildSnowFlake) {
            featureList
                .flatMap { feature -> feature.otherCommands + listOfNotNull(feature.defaultCommand) }
                .distinctBy { it.command.name.lowercase() }
                .forEach { supportedCommand ->
                    input(
                        name = supportedCommand.command.name.lowercase(),
                        description = supportedCommand.description
                    ) {
                        supportedCommand.arguments.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                            }
                        }
                    }
                }
        }.collect()
    }

    private suspend fun createGlobalCommands() {
        kord.createGlobalApplicationCommands {
            featureList
                .flatMap { feature -> feature.otherCommands + listOfNotNull(feature.defaultCommand) }
                .distinctBy { it.command.name.lowercase() }
                .forEach { supportedCommand ->
                    input(
                        name = supportedCommand.command.name.lowercase(),
                        description = supportedCommand.description
                    ) {
                        supportedCommand.arguments.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                            }
                        }
                    }
                }
        }.collect()
    }


    private companion object {
        const val TAG = "DiscordBot"
    }
}
