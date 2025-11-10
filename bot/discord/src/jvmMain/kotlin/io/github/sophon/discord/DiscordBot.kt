package io.github.sophon.discord

import TEST_SERVER_ID
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
import io.github.sophon.core.util.isAtLeast
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.util.removeTag
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface DiscordBot {
    suspend fun startSession()
}

internal class DiscordBotImpl(
    private val apiKey: String,
    private val featureList: List<DiscordRegisteredFeature>,
): DiscordBot {
    private lateinit var kord: Kord

    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        coroutineScope {
            featureList.forEach { feature ->
                launch { feature.start() }
            }
        }
        startKord()
    }


    private suspend fun startKord() {
        kord = Kord(token = apiKey)

        // Delete old guild commands FIRST (outside the builder)
        val testGuildSnowFlake = Snowflake(TEST_SERVER_ID)
        kord.getGuildApplicationCommands(testGuildSnowFlake).collect { command ->
            command.delete()
        }

        createGlobalCommands()
        createCommandsForTestServer(testGuildSnowFlake)
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

    private suspend fun MessageCreateEvent.handleMessage() {
        if (kord.selfId !in message.mentionedUserIds) return

        val rawQuery = message.content.removeTag().takeIf { it.isAtLeast(wordCount = 2) } ?: return
        val firstWord = rawQuery.substringBefore(' ')
        val command = Command.entries.find {
            it.name.equals(firstWord, ignoreCase = true)
        } ?: Command.FD

        /**
         * raw = "kaz 112" -> command = FD, query = "kaz 112"
         *
         * raw = "gl fireball" -> command = GL, query = "fireball"
         */
        val query = when (command) {
            Command.FD -> firstWord.lowercase() + rawQuery.substring(firstWord.length)
            else -> rawQuery.substringAfter(' ', rawQuery)
        }
        val service = featureList.first { feature ->
            feature.slashCommands.any { it.name == command }
        }
        val embedBuilder = service.execute(command, query)

        message.channel.createMessage {
            messageReference = message.id
            allowedMentions { repliedUser = false }
            embed(embedBuilder)
        }
    }

    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        val command = Command.entries
            .find { it.name.equals(interaction.command.rootName, ignoreCase = true) }
            ?: return //this should NEVER happen
        val service = featureList.first { feature ->
            feature.slashCommands.any { it.name == command }
        }
        val args = interaction.command.strings
        val query = service.buildQuery(args, command)
        val embedBuilder = service.execute(command, query)

        interaction.respondPublic { embed(embedBuilder) }
    }

    private suspend fun createCommandsForTestServer(testGuildSnowFlake: Snowflake) {
        kord.createGuildApplicationCommands(testGuildSnowFlake) {
            featureList.forEach { feature ->
                feature.slashCommands.forEach { slashCommand ->
                    input(
                        name = slashCommand.name.name.lowercase(),
                        description = slashCommand.description
                    ) {
                        slashCommand.arguments.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                            }
                        }
                    }
                }
            }
        }.collect()
    }

    private suspend fun createGlobalCommands() {
        kord.createGlobalApplicationCommands {
            featureList.forEach { feature ->
                feature.slashCommands.forEach { slashCommand ->
                    input(
                        name = slashCommand.name.name.lowercase(),
                        description = slashCommand.description
                    ) {
                        slashCommand.arguments.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                            }
                        }
                    }
                }
            }
        }.collect()
    }
}

private const val TAG = "DiscordBot"