package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
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
import io.github.sophon.discord.COMMAND_MAX_SUGGESTIONS
import io.github.sophon.discord.adapter.inbound.kord.ui.characterEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveEmbed
import io.github.sophon.discord.adapter.inbound.kord.ui.moveListEmbed
import io.github.sophon.discord.adapter.inbound.scheduler.Scheduler
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.adminCommands
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.discord.Command.Argument.AutoCompleteType
import io.github.sophon.discord.app.model.discord.DiscordCommandInteraction
import io.github.sophon.discord.app.model.discord.Message
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.response.CoreResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.discord.app.model.response.MediaResponse
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.model.response.SteamLobbyResponse
import io.github.sophon.discord.app.model.response.UnbanResponse
import io.github.sophon.discord.app.service.AdminService
import io.github.sophon.discord.app.util.kordRestCall
import io.github.sophon.discord.inPort.ProcessUserInputUseCase
import io.github.sophon.discord.inPort.ProduceAutoCompleteUseCase
import io.github.sophon.discord.inPort.StartFeaturesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import java.lang.management.ManagementFactory
import kotlin.time.Duration.Companion.hours

internal interface DiscordBot {
    suspend fun startSession()
}

internal class DiscordBotImpl(
    private val kord: Kord,
    private val coroutineScope: CoroutineScope,
    private val scheduler: Scheduler,
    private val commandRegistry: CommandRegistry,

    private val kordResponder: KordResponder,
    private val adminService: AdminService,
    private val startFeaturesUseCase: StartFeaturesUseCase,
    private val processUserInputUseCase: ProcessUserInputUseCase,
    private val discordButtonHandler: DiscordButtonHandler,
    private val produceAutoCompleteUseCase: ProduceAutoCompleteUseCase,
) : DiscordBot {
    override suspend fun startSession() {
        Napier.i(tag = TAG) { "🚀 Bot starting..." }

        startFeatures()
        startMemoryLogging()
        startKord()

        Napier.e(tag = TAG) { "❌ Bot session ended (this shouldn't happen)" }
    }


    private suspend fun startFeatures() {
        startFeaturesUseCase()
            .onError { error -> Napier.e(tag = TAG) { "Feature start failed: $error" } }
    }

    private suspend fun startKord() {
        val adminConfigResult = adminService.getAdminConfig()
            .onError { error -> Napier.e(tag = TAG) { "Admin config load failed: $error" } }

        adminConfigResult.onSuccess { adminConfig -> cleanOldGuildCommands(kord, adminConfig.adminServerId) }
        createGlobalCommands()
        adminConfigResult.onSuccess { adminConfig ->
            createAdminCommands(adminConfig.adminServerId)
//            createCommandsForTestServer(adminConfig.adminServerId)
        }

        monitorGatewayHealth()

        kord.on<GuildChatInputCommandInteractionCreateEvent> {
            processInteraction(interaction)
        }

        kord.on<MessageCreateEvent> {
            processMessage(message)
        }

        kord.on<ButtonInteractionCreateEvent> {
            discordButtonHandler.process(interaction)
        }

        kord.on<AutoCompleteInteractionCreateEvent> {
            processAutoComplete(interaction)
        }

        //‼️ THIS SUSPENDS UNTIL LOGGED OUT
        try {
            kord.login {
                @OptIn(PrivilegedIntent::class)

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

    //TODO: need to extract this somewhere
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
                        is MoveResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = moveEmbed(response),
                                imageList = response.hitboxImageList,
                                isExpanded = response.forceExpand,
                                buttonSet = response.buttonSet,
                            )
                        }

                        is ListResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = moveListEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = response.buttonSet,
                            )
                        }

                        is CharacterResponse -> {
                            kordResponder.respond(
                                message = message,
                                embedBuilder = characterEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = null,
                            )
                        }

                        is CoreResponse -> {
                            kordResponder.respond(
                                message = message,
                                coreResponse = response,
                            )
                        }

                        is ModulesResponse -> {
                            kordResponder.respond(
                                message = message,
                                modulesResponse = response,
                            )
                        }

                        is AliasResponse -> {
                            kordResponder.respond(
                                message = message,
                                aliasResponse = response,
                            )
                        }

                        is SteamLobbyResponse -> {
                            kordResponder.respond(
                                message = message,
                                steamLobby = response,
                            )
                        }

                        is PlainTextResponse -> {
                            kordResponder.respond(
                                message = message,
                                plainText = response,
                            )
                        }

                        is FeedbackResponse -> {
                            kordResponder.respond(
                                message = message,
                                feedback = response,
                            )
                        }

                        is ReplyResponse -> {
                            kordResponder.respond(
                                message = message,
                                reply = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is BanResponse -> {
                            kordResponder.respond(
                                message = message,
                                ban = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is UnbanResponse -> {
                            kordResponder.respond(
                                message = message,
                                unban = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is EwgfResponse -> {
                            kordResponder.respond(
                                message = message,
                                ewgfResponse = response,
                            )
                        }

                        is GlossaryResponse -> {
                            kordResponder.respond(
                                message = message,
                                glossaryResponse = response,
                            )
                        }

                        is MediaResponse -> {
                            kordResponder.respond(
                                message = message,
                                mediaResponse = response,
                            )
                        }

                        else -> {}
                    }
                }
                .onError { botError ->
                    handleError(
                        message = message,
                        serverName = userMessage.serverName,
                        botError = botError,
                    )
                }
        }
    }

    //TODO: need to split this somewhere
    private suspend fun processInteraction(interaction: GuildChatInputCommandInteraction) {
        kordRestCall(
            tag = TAG,
            onForbidden = { errorMessage -> kordResponder.whisper(interaction = interaction, errorMessage = errorMessage) },
        ) {
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
                        is MoveResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = moveEmbed(response),
                                imageList = response.hitboxImageList,
                                isExpanded = response.forceExpand,
                                buttonSet = response.buttonSet,
                            )
                        }

                        is ListResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = moveListEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = response.buttonSet,
                            )
                        }

                        is CharacterResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                embedBuilder = characterEmbed(response),
                                imageList = emptyList(),
                                isExpanded = false,
                                buttonSet = null,
                            )
                        }

                        is CoreResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                coreResponse = response,
                            )
                        }

                        is ModulesResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                modulesResponse = response,
                            )
                        }

                        is AliasResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                aliasResponse = response,
                            )
                        }

                        is SteamLobbyResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                steamLobby = response,
                            )
                        }

                        is PlainTextResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                plainText = response,
                            )
                        }

                        is FeedbackResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                feedback = response,
                            )
                        }

                        is ReplyResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                reply = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is BanResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                ban = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is UnbanResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                unban = response,
                            ).onError { error -> Napier.e(tag = TAG) { "Post failed: $error" } }
                        }

                        is EwgfResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                ewgfResponse = response,
                            )
                        }

                        is GlossaryResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                glossaryResponse = response,
                            )
                        }

                        is MediaResponse -> {
                            kordResponder.respond(
                                interaction = interaction,
                                mediaResponse = response,
                            )
                        }

                        else -> {}
                    }
                }
                .onError { botError ->
                    handleError(
                        interaction = interaction,
                        serverName = discordCommandInteraction.serverName,
                        botError = botError,
                    )
                }
        }
    }

    private suspend fun handleError(
        message: dev.kord.core.entity.Message,
        serverName: String?,
        botError: BotError,
    ) {
        val server = serverName?.takeIf { it.isNotBlank() } ?: UNKNOWN_SERVER
        Napier.e(tag = TAG) { "$server: $botError" }

        kordResponder.respond(
            message = message,
            botError = botError,
        )
    }

    private suspend fun handleError(
        interaction: GuildChatInputCommandInteraction,
        serverName: String?,
        botError: BotError,
    ) {
        val server = serverName?.takeIf { it.isNotBlank() } ?: UNKNOWN_SERVER
        Napier.e(tag = TAG) { "$server: $botError" }

        kordResponder.respond(
            interaction = interaction,
            botError = botError,
        )
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

    private suspend fun cleanOldGuildCommands(kord: Kord, adminServerId: String) = try {
        val testGuildSnowFlake = Snowflake(adminServerId)
        kord.getGuildApplicationCommands(testGuildSnowFlake).collect { command ->
            try {
                command.delete()
            } catch (e: Exception) {
                Napier.e(tag = TAG) { "Failed to delete command ${command.name}: ${e.message}" }
            }
        }
    } catch (e: Exception) {
        Napier.e(tag = TAG, throwable = e) { "Failed to delete old commands" }
    }

    @Suppress("UnusedPrivateMember")
    private suspend fun createCommandsForTestServer(adminServerId: String) {
        val testGuildSnowFlake = Snowflake(adminServerId)
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

    private suspend fun createAdminCommands(adminServerId: String) {
        try {
            val adminGuildSnowFlake = Snowflake(adminServerId)
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
        const val UNKNOWN_SERVER = "(Unknown server)"
    }
}
