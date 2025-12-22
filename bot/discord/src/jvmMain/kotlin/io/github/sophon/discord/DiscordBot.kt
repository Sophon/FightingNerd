package io.github.sophon.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.admin.adminCommands
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.discord.util.createEmbedMessage
import io.github.sophon.discord.util.createEmbedResponse
import io.github.sophon.discord.util.createErrorEmbed
import io.github.sophon.discord.util.createPlainMessage
import io.github.sophon.discord.util.createPlainResponse
import io.github.sophon.discord.util.delete
import io.github.sophon.discord.util.deleteInteraction
import io.github.sophon.domain.Source
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface DiscordBot {
    suspend fun startSession()
}

internal class DiscordBotImpl(
    private val kord: Kord,
    private val featureList: List<DiscordRegisteredFeature>,
    private val adminConfig: Config.AdminConfig?,
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
        createAdminCommands()
//        createCommandsForTestServer()

        monitorGatewayHealth()

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

            presence {
                playing("/FD | /HELP | /DONATE")
            }
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

        val source = Source(
            username = message.author?.username.orEmpty(),
            id = message.author?.id.toString(),
            channelId = message.channelId.toString(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source,
            message.content.lowercase(),
        )

        val botOutput = when(result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                BotOutput(errorEmbedBuilder = createErrorEmbed(result.error))
            }
        }

        when {
            botOutput.embedBuilder != null -> {
                createEmbedMessage(botOutput.embedBuilder, botOutput.images)
            }
            botOutput.plainText != null -> {
                createPlainMessage(botOutput.plainText)
            }
            botOutput.errorEmbedBuilder != null -> {
                createEmbedMessage(botOutput.errorEmbedBuilder)
                    .delete(delay = TIME_DELETE_ERROR_EMBED, scope = kord)
            }
            botOutput.feedback != null -> {
                createEmbedMessage(botOutput.feedback)
            }
            botOutput.reply != null -> {
                createEmbedMessage(botOutput.reply)
            }
        }
    }

    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        val commandString = interaction.command.rootName
            .lowercase()
        val query = interaction.command.strings.values
            .joinToString(" ")
            .lowercase()
        val source = Source(
            username = interaction.user.username,
            id = interaction.user.data.id.toString(),
            channelId = interaction.channelId.toString(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source = source,
            commandString = commandString,
            query = query
        )

        val botOutput = when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                BotOutput(errorEmbedBuilder = createErrorEmbed(result.error))
            }
        }

        when {
            botOutput.embedBuilder != null -> {
                createEmbedResponse(botOutput.embedBuilder, botOutput.images)
            }
            botOutput.plainText != null -> {
                createPlainResponse(botOutput.plainText)
            }
            botOutput.errorEmbedBuilder != null -> {
                createEmbedResponse(botOutput.errorEmbedBuilder)
                deleteInteraction(delay = TIME_DELETE_ERROR_EMBED, scope = kord)
            }
            botOutput.feedback != null -> {
                createEmbedResponse(botOutput.feedback)
            }
            botOutput.reply != null -> {
                createEmbedResponse(botOutput.reply)
            }
        }
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
                .filter { supportedCommand ->
                    adminCommands.contains(supportedCommand).not()
                }
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

    private suspend fun createAdminCommands() {
        if (adminConfig == null) return

        val adminGuildSnowFlake = Snowflake(TEST_SERVER_ID)
        kord.createGuildApplicationCommands(adminGuildSnowFlake) {
            adminCommands.forEach { command ->
                input(
                    name = command.command.name.lowercase(),
                    description = command.description
                ) {
                    command.arguments.forEach { argument ->
                        string(name = argument.name, description = argument.description) {
                            required = argument.isRequired
                        }
                    }
                }
            }
        }.collect()
    }

    private fun monitorGatewayHealth() {
        kord.on<DisconnectEvent.RetryLimitReachedEvent> {
            Napier.e(tag = TAG) { "Gateway failed to recover on shard $shard - retry limit reached" }
        }

        kord.on<DisconnectEvent.DiscordCloseEvent> {
            if (recoverable.not()) {
                Napier.e(tag = TAG) { "Gateway closed non-recoverably on shard $shard; code = $closeCode" }
            }
        }
    }


    private companion object {
        const val TAG = "DiscordBot"
    }
}
