package io.github.sophon.discord.feat.bot

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.gateway.ResumedEvent
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.discord.feat.admin.adminCommands
import io.github.sophon.discord.feat.bot.usecase.HandleAutoCompleteEventUseCase
import io.github.sophon.discord.feat.bot.usecase.HandleButtonInteractionUseCase
import io.github.sophon.discord.feat.bot.usecase.HandleMessageUseCase
import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.ResultToEmbedUseCase
import io.github.sophon.discord.feat.bot.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.Tracker
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.safeRestCall
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

internal interface DiscordBot {
    suspend fun startSession()
}

@OptIn(ExperimentalUuidApi::class)
internal class DiscordBotImpl(
    private val kord: Kord,
    private val tracker: Tracker,
    private val adminConfig: Config.AdminConfig,
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
    private val resultToEmbedUseCase: ResultToEmbedUseCase,
    private val handleMessageUseCase: HandleMessageUseCase,
    private val handleAutoCompleteEventUseCase: HandleAutoCompleteEventUseCase,
    private val handleButtonInteractionUseCase: HandleButtonInteractionUseCase,
    private val postDailyReportEmbedUseCase: PostDailyReportEmbedUseCase,
    private val coroutineScope: CoroutineScope,
    private val botFeatureRepo: BotFeatureRepo,
): DiscordBot {
    private val editableEmbedMap = mutableMapOf<String, BotOutput>()

    override suspend fun startSession() {
        Napier.i(tag = TAG) { "🚀 Bot starting..." }

        startFeatures()
        startTracking()
        startKord()

        Napier.e(tag = TAG) { "❌ Bot session ended (this shouldn't happen)" }
    }


    private suspend fun startFeatures() {
        botFeatureRepo.initialize()
    }

    private suspend fun startKord() {
        cleanOldGuildCommands(kord)
        createGlobalCommands()
        createAdminCommands()
//        createCommandsForTestServer()

        monitorGatewayHealth()

        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            safeRestCall(TAG) { handleCommand() }
        }

        kord.on<MessageCreateEvent> {
            safeRestCall(TAG) {
                with(handleMessageUseCase) { invoke(editableEmbedMap) }
            }
        }

        kord.on<ButtonInteractionCreateEvent> {
            handleButtonInteractionUseCase.invoke(interaction, editableEmbedMap, coroutineScope)
                .onError { error ->
                    Napier.e(tag = TAG) { "${interaction.data.guildId} → Button interaction: $error" }
                }
        }

        kord.on<AutoCompleteInteractionCreateEvent> {
            safeRestCall(TAG) {
                with(handleAutoCompleteEventUseCase) { invoke() }
            }
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        try {
            kord.login {
                @OptIn(PrivilegedIntent::class)
//                intents += Intent.MessageContent //TODO: enable once verified

                presence {
                    playing("/FD | /HELP | /FEEDBACK")
                }
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "💥 Login failed: ${e.message}" }
            throw e
        }

        Napier.e(tag = TAG) { "⚠️ Login ended (bot disconnected)" }
    }

    private suspend fun cleanOldGuildCommands(kord: Kord) = try {
        val testGuildSnowFlake = Snowflake(adminConfig.adminServerId)
        kord.getGuildApplicationCommands(testGuildSnowFlake).collect { command ->
            try {
                command.delete()
            } catch (e: Exception) {
                Napier.e(tag = TAG) { "Failed to delete command ${command.name}: ${e.message}" }
            }
        }
    } catch(e: Exception) {
        Napier.e(tag = TAG, throwable = e) { "Failed to delete old commands" }
    }

    //TODO: to usecase?
    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        val commandString = interaction.command.rootName
            .lowercase()
        val query = interaction.command.strings.values
            .joinToString(" ")
        val source = Source(
            username = interaction.user.username,
            id = interaction.user.data.id.toString(),
            channelId = interaction.channelId.toString(),
            serverName = interaction.getGuildOrNull()?.name.orEmpty(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source = source,
            commandString = commandString,
            query = query
        )

        with (resultToEmbedUseCase) {
            invoke(source, result, coroutineScope, editableEmbedMap)
        }
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun createCommandsForTestServer() {
        val testGuildSnowFlake = Snowflake(adminConfig.adminServerId)
        val featureList = botFeatureRepo.getFeatures()
        kord.createGuildApplicationCommands(testGuildSnowFlake) {
            featureList
                .flatMap { feature -> feature.otherCommands + listOfNotNull(feature.defaultCommand) }
                .distinctBy { it.name.lowercase() }
                .forEach { supportedCommand ->
                    input(
                        name = supportedCommand.name.lowercase(),
                        description = supportedCommand.description
                    ) {
                        if (adminCommands.contains(supportedCommand)) {
                            defaultMemberPermissions = Permissions(Permission.Administrator)
                        }

                        supportedCommand.argumentList.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                                autocomplete = argument.hasAutocomplete
                            }
                        }
                    }
                }
        }.collect()
    }

    private suspend fun createGlobalCommands() {
        try {
            val featureList = botFeatureRepo.getFeatures()
            kord.createGlobalApplicationCommands {
                featureList
                    .flatMap { feature -> feature.otherCommands + listOfNotNull(feature.defaultCommand) }
                    .distinctBy { it.name.lowercase() }
                    .filter { supportedCommand ->
                        adminCommands.contains(supportedCommand).not()
                    }
                    .forEach { supportedCommand ->
                        input(
                            name = supportedCommand.name.lowercase(),
                            description = supportedCommand.description
                        ) {
                            supportedCommand.argumentList.forEach { argument ->
                                string(name = argument.name, description = argument.description) {
                                    required = argument.isRequired
                                }
                            }
                        }
                    }
            }.collect()
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "Failed to create global commands: ${e.message}" }
        }
    }

    private suspend fun createAdminCommands() {
        try {
            val adminGuildSnowFlake = Snowflake(adminConfig.adminServerId)
            kord.createGuildApplicationCommands(adminGuildSnowFlake) {
                adminCommands.forEach { command ->
                    input(
                        name = command.name.lowercase(),
                        description = command.description
                    ) {
                        defaultMemberPermissions = Permissions(Permission.Administrator)

                        command.argumentList.forEach { argument ->
                            string(name = argument.name, description = argument.description) {
                                required = argument.isRequired
                                autocomplete = argument.hasAutocomplete
                            }
                        }
                    }
                }
            }.collect()
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "Failed to create admin commands: ${e.message}" }
        }
    }

    private fun monitorGatewayHealth() {
        kord.on<DisconnectEvent.RetryLimitReachedEvent> {
            Napier.e(tag = TAG) { "Gateway failed to recover on shard $shard - retry limit reached" }
        }

        kord.on<DisconnectEvent.DiscordCloseEvent> {
            if (recoverable.not()) {
                Napier.e(tag = TAG) {
                    "Non-recoverable disconnect on shard $shard: code=${closeCode.code} ($closeCode)"
                }
            } else {
                Napier.d(tag = TAG) {
                    "Gateway disconnect on shard $shard: code=${closeCode.code}, will reconnect"
                }
            }
        }

        kord.on<ResumedEvent> {
            Napier.i(tag = TAG) { "Gateway resumed successfully" }
        }
    }

    private fun startTracking() {
        coroutineScope.launch {
            tracker.subscribe().collectLatest { dailyReport ->
                postDailyReportEmbedUseCase.invoke(
                    statsChannelId = tracker.statsChannelId,
                    dailyReport = dailyReport,
                )
            }
        }
    }


    private companion object {
        const val TAG = "DiscordBot"
    }
}
