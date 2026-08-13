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
import io.github.sophon.discord.feat.bot.usecase.HandleQueryUseCase
import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.Tracker
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command.Argument.AutoCompleteType
import io.github.sophon.discord.util.safeRestCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.lang.management.ManagementFactory
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.ExperimentalUuidApi

internal interface DiscordBot {
    suspend fun startSession()
}

@OptIn(ExperimentalUuidApi::class)
internal class DiscordBotImpl(
    private val kord: Kord,
    private val tracker: Tracker,
    private val adminConfig: Config.AdminConfig,
    private val handleQueryUseCase: HandleQueryUseCase,
    private val handleAutoCompleteEventUseCase: HandleAutoCompleteEventUseCase,
    private val handleButtonInteractionUseCase: HandleButtonInteractionUseCase,
    private val postDailyReportEmbedUseCase: PostDailyReportEmbedUseCase,
    private val coroutineScope: CoroutineScope,
    private val botFeatureRepo: BotFeatureRepo,
    private val scheduler: Scheduler,
): DiscordBot {
    private val editableEmbedMap = mutableMapOf<String, BotOutput>()

    override suspend fun startSession() {
        Napier.i(tag = TAG) { "🚀 Bot starting..." }

        startFeatures()
        startTracking()
        startMemoryLogging()
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
            handleQueryUseCase.invoke(
                interaction = interaction,
                editableEmbedMap = editableEmbedMap,
            )
        }

        kord.on<MessageCreateEvent> {
            safeRestCall(TAG) {
                handleQueryUseCase.invoke(
                    message = message,
                    botId = kord.selfId,
                    editableEmbedMap = editableEmbedMap,
                )
            }
        }

        kord.on<ButtonInteractionCreateEvent> {
            safeRestCall(TAG) {
                handleButtonInteractionUseCase.invoke(interaction, editableEmbedMap, coroutineScope)
                    .onError { error ->
                        Napier.e(tag = TAG) { "${interaction.data.guildId} → Button interaction: $error" }
                    }
            }
        }

        kord.on<AutoCompleteInteractionCreateEvent> {
            safeRestCall(TAG) {
                handleAutoCompleteEventUseCase.invoke(interaction)
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
                                autocomplete = (argument.autoCompleteType != AutoCompleteType.None)
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
                                    autocomplete = (argument.autoCompleteType != AutoCompleteType.None)
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
                                autocomplete = (argument.autoCompleteType != AutoCompleteType.None)
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

    private fun startMemoryLogging() {
        scheduler.start(
            period = 3.hours,
            task = {
                val runtime = Runtime.getRuntime()
                val heapUsed = runtime.totalMemory() - runtime.freeMemory()
                val heapCommitted = runtime.totalMemory()
                val heapMax = runtime.maxMemory()

                val nonHeap = ManagementFactory.getMemoryMXBean().nonHeapMemoryUsage
                val nonHeapUsed = nonHeap.used
                val nonHeapCommitted = nonHeap.committed
                Napier.i(tag = TAG) {
                    "Heap: used=${heapUsed / 1024 / 1024}MB committed=${heapCommitted / 1024 / 1024}MB max=${heapMax / 1024 / 1024}MB " +
                            "NonHeap: used=${nonHeapUsed / 1024 / 1024}MB committed=${nonHeapCommitted / 1024 / 1024}MB"
                }
            }
        ).launchIn(coroutineScope)
    }


    private companion object {
        const val TAG = "DiscordBot"
    }
}
