package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
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
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.adapter.inbound.kord.ui.characterEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveListEmbed
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ButtonEvent
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.Command.Argument.AutoCompleteType
import io.github.sophon.discord.app.domain.model.DiscordCommandInteraction
import io.github.sophon.discord.app.domain.model.Message
import io.github.sophon.discord.app.port.inbound.ProcessButtonEventUseCase
import io.github.sophon.discord.app.port.inbound.ProcessUserInputUseCase
import io.github.sophon.discord.app.port.inbound.ProduceAutoCompleteUseCase
import io.github.sophon.discord.app.port.inbound.StartFeaturesUseCase
import io.github.sophon.discord.app.domain.model.adminCommands
import io.github.sophon.discord.feat.bot.usecase.PostDailyReportEmbedUseCase
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.Tracker
import io.github.sophon.discord.util.kordRestCall
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
    private val postDailyReportEmbedUseCase: PostDailyReportEmbedUseCase,
    private val coroutineScope: CoroutineScope,
    private val scheduler: Scheduler,
    private val commandRegistry: CommandRegistry,

    private val kordResponder: KordResponder,
    private val startFeaturesUseCase: StartFeaturesUseCase,
    private val processUserInputUseCase: ProcessUserInputUseCase,
    private val processButtonEventUseCase: ProcessButtonEventUseCase,
    private val produceAutoCompleteUseCase: ProduceAutoCompleteUseCase,
): DiscordBot {
    override suspend fun startSession() {
        Napier.i(tag = TAG) { "🚀 Bot starting..." }

        startFeatures()
        startTracking()
        startMemoryLogging()
        startKord()

        Napier.e(tag = TAG) { "❌ Bot session ended (this shouldn't happen)" }
    }


    private fun startFeatures() {
//        botFeatureRepo.initialize()

        coroutineScope.launch {
            startFeaturesUseCase()
                .onError { error -> Napier.e(tag = TAG) { "Feature start failed: $error" } }
        }
    }

    private suspend fun startKord() {
        cleanOldGuildCommands(kord)
        createGlobalCommands()
        createAdminCommands()
//        createCommandsForTestServer()

        monitorGatewayHealth()

        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            processInteraction(interaction)
        }

        kord.on<MessageCreateEvent> {
            processMessage(message)
        }

        kord.on<ButtonInteractionCreateEvent> {
            processButtonEvent(interaction)
        }

        kord.on<AutoCompleteInteractionCreateEvent> {
            processAutoComplete(interaction)
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

    private suspend fun processMessage(message: dev.kord.core.entity.Message) {
        kordRestCall(TAG) {
            val userMessage = Message(
                serverName = message.getGuildOrNull()?.name.orEmpty(),
                channelId = message.channelId.toString(),
                author = Message.Author(
                    id = message.author?.id?.toString().orEmpty(),
                    username = message.author?.username.orEmpty(),
                ),
                // webhook messages have no author, treat them as bots
                isFromBot = (message.author?.isBot ?: true),
                content = message.content,
            )
            processUserInputUseCase(
                message = userMessage,
                botId = kord.selfId.toString(),
            )
                .onSuccess { response ->
                    when (response) {
                        is BotResponse.MoveResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = moveEmbed(response),
                                imageList = response.hitboxImageList,
                                isExpanded = (response.isCollapsedByDefault.not() || response.forceExpand),
                                buttonSet = response.buttonSet,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.ListResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = moveListEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = response.buttonSet,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.CharacterResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = characterEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = null,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.CoreResponse -> {
                            kordResponder.respond(
                                message = message,
                                coreResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.ModulesResponse -> {
                            kordResponder.respond(
                                message = message,
                                modulesResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.AliasResponse -> {
                            kordResponder.respond(
                                message = message,
                                aliasResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.SteamLobby -> {
                            kordResponder.respond(
                                message = message,
                                steamLobby = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.PlainText -> {
                            kordResponder.respond(
                                message = message,
                                plainText = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        else -> {}
                    }
                }
                .onError { botError ->
                    kordResponder.respond(
                        message = message,
                        botError = botError,
                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                }
        }
    }

    private suspend fun processInteraction(interaction: GuildChatInputCommandInteraction) {
        kordRestCall(TAG) {
            val discordCommandInteraction = DiscordCommandInteraction(
                username = interaction.user.username,
                userId = interaction.user.id.toString(),
                channelId = interaction.channelId.toString(),
                command = interaction.command.rootName,
                argumentMap = interaction.command.strings,
                serverName = interaction.getGuildOrNull()?.name,
            )
            processUserInputUseCase(discordCommandInteraction = discordCommandInteraction)
                .onSuccess { response ->
                    when (response) {
                        is BotResponse.MoveResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = moveEmbed(response),
                                imageList = response.hitboxImageList,
                                isExpanded = (response.isCollapsedByDefault.not() || response.forceExpand),
                                buttonSet = response.buttonSet,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.ListResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = moveListEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = response.buttonSet,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.CharacterResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = characterEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = null,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.CoreResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                coreResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.ModulesResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                modulesResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.AliasResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                aliasResponse = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.SteamLobby -> {
                            kordResponder.respond(
                                interaction = interaction,
                                steamLobby = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        is BotResponse.PlainText -> {
                            kordResponder.respond(
                                interaction = interaction,
                                plainText = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }
                        else -> {}
                    }
                }
                .onError { botError ->
                    kordResponder.respond(
                        interaction = interaction,
                        botError = botError,
                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                }
        }
    }

    private suspend fun processButtonEvent(interaction: ButtonInteraction) {
        kordRestCall(TAG) {
            val buttonEvent = decodeToButtonEvent(buttonId = interaction.componentId)
            if (buttonEvent == null) {
                Napier.w(tag = TAG) { "Unknown button: ${interaction.componentId}" }
                return@kordRestCall
            }

            val deferredResponse = interaction.deferPublicMessageUpdate()
            processButtonEventUseCase(buttonEvent)
                .onSuccess { response ->
                    when (buttonEvent) {
                        is ButtonEvent.Expand -> {
                            if (response is BotResponse.MoveResponse) {
                                kordResponder.edit(
                                    message = interaction.message,
                                    embedBuilder = moveEmbed(response),
                                    imageList = response.hitboxImageList,
                                    isExpanded = (response.isCollapsedByDefault.not() || response.forceExpand),
                                    buttonSet = response.buttonSet,
                                ).onError { error -> Napier.e(tag = TAG) { "Edit failed: $error" } }
                            }
                        }

                        is ButtonEvent.Query -> {
                            if (response is BotResponse.MoveResponse) {
                                kordResponder.respond(
                                    message = interaction.message,
                                    embedBuilder = moveEmbed(response),
                                    imageList = response.hitboxImageList,
                                    isExpanded = (response.isCollapsedByDefault.not() || response.forceExpand),
                                    buttonSet = response.buttonSet,
                                ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                            }
                        }

                        is ButtonEvent.Command -> {
                            when (response) {
                                is BotResponse.MoveResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = moveEmbed(response),
                                        imageList = response.hitboxImageList,
                                        isExpanded = (response.isCollapsedByDefault.not() || response.forceExpand),
                                        buttonSet = response.buttonSet,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                is BotResponse.ListResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = moveListEmbed(response),
                                        imageList = emptyList(),
                                        isExpanded = false,
                                        buttonSet = response.buttonSet,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                is BotResponse.CharacterResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        embedBuilder = characterEmbed(response),
                                        imageList = emptyList(),
                                        isExpanded = false,
                                        buttonSet = null,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                is BotResponse.CoreResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        coreResponse = response,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                is BotResponse.ModulesResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        modulesResponse = response,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                is BotResponse.AliasResponse -> {
                                    kordResponder.respond(
                                        message = interaction.message,
                                        aliasResponse = response,
                                    ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                                }
                                else -> {}
                            }
                        }

                        is ButtonEvent.Text -> {
                            if (response is BotResponse.PlainText) {
                                kordResponder.respondText(
                                    response = deferredResponse,
                                    mention = interaction.user.mention,
                                    text = response.text,
                                ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                            }
                        }
                    }
                }
                .onError { error -> Napier.e(tag = TAG) { "Button event failed: $error" } }
        }
    }

    private suspend fun processAutoComplete(interaction: AutoCompleteInteraction) {
        kordRestCall(TAG) {
            val focusedArgumentName = interaction.command.options.entries
                .firstOrNull { it.value.focused }
                ?.key
                .orEmpty()
            val query = interaction.focusedOption.value.trim()

            val choices = produceAutoCompleteUseCase(
                commandString = interaction.command.rootName,
                argument = focusedArgumentName,
                query = query,
                argumentMap = interaction.command.strings,
            ).take(COMMAND_MAX_SUGGESTIONS)

            interaction.suggestString {
                choices.forEach { choice(it.name, it.value) }
            }
        }
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
        kord.createGuildApplicationCommands(testGuildSnowFlake) {
            Command.entries
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
        }.collect { registered ->
            commandRegistry.put(registered.name, registered.id)
        }
    }

    private suspend fun createGlobalCommands() {
        try {
            kord.createGlobalApplicationCommands {
                Command.entries
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
            }.collect { registered ->
                commandRegistry.put(registered.name, registered.id)
            }
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
                kordRestCall(TAG) {
                    postDailyReportEmbedUseCase.invoke(
                        statsChannelId = tracker.statsChannelId,
                        dailyReport = dailyReport,
                    )
                }
            }
        }
    }

    private fun startMemoryLogging() {
        scheduler.start(
            period = 2.hours,
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
