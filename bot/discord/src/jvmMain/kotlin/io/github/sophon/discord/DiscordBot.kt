package io.github.sophon.discord

import dev.kord.common.Color
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.embed
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.Config
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.admin.adminCommands
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_EDIT
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_QUERY
import io.github.sophon.discord.usecase.ResultToEmbedUseCase
import io.github.sophon.discord.usecase.RouteCommandToFeatureUseCase
import io.github.sophon.discord.util.createButtons
import io.github.sophon.discord.util.safeRestCall
import io.github.sophon.domain.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface DiscordBot {
    suspend fun startSession()
}

@OptIn(ExperimentalUuidApi::class)
internal class DiscordBotImpl(
    private val kord: Kord,
    private val featureList: List<DiscordRegisteredFeature>,
    private val adminConfig: Config.AdminConfig,
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
    private val resultToEmbedUseCase: ResultToEmbedUseCase,
    private val coroutineScope: CoroutineScope,
): DiscordBot {
    private val editableEmbedMap = mutableMapOf<String, BotOutput>()

    override suspend fun startSession() {
        Napier.i(tag = TAG) { "🚀 Bot starting..." }

        startFeatures()
        startKord()

        Napier.e(tag = TAG) { "❌ Bot session ended (this shouldn't happen)" }
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
            safeRestCall(TAG) { handleCommand() }
        }

        kord.on<MessageCreateEvent> {
            // ignoring other bots, even ourselves
            if (message.author?.isBot != false) return@on

            // ignoring if someone replies with tag
            val botId = kord.selfId
            val botMention = "<@$botId>"
            val botNicknameMention = "<@!$botId>"
            if (botMention !in message.content && botNicknameMention !in message.content) {
                return@on
            }

            safeRestCall(TAG) { handleMessage() }
        }

        kord.on<ButtonInteractionCreateEvent> {
            safeRestCall(TAG) { handleButton() }
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
            e.printStackTrace()
            throw e
        }

        Napier.e(tag = TAG) { "⚠️ Login ended (bot disconnected)" }
    }

    private suspend fun cleanOldGuildCommands(kord: Kord) {
        val testGuildSnowFlake = Snowflake(adminConfig.adminServerId)
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
            serverName = message.getGuildOrNull()?.name.orEmpty(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source = source,
            message = message.content.lowercase(),
        )

        with (resultToEmbedUseCase) {
            invoke(source, result, coroutineScope, editableEmbedMap)
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

    private suspend fun ButtonInteractionCreateEvent.handleButton() {
        val source = Source(
            username = interaction.user.username,
            id = interaction.user.data.id.toString(),
            channelId = interaction.channelId.toString(),
        )
        val action = interaction.componentId
        val message = interaction.data.message.value?.let {
            interaction.message
        }

        when {
            (KEY_QUERY in action) -> {
                val response = interaction.deferPublicResponse()
                val message = action.substringAfter(KEY_QUERY)
                routeCommandToFeatureUseCase.invoke(source, message)
                    .onSuccess { botOutput ->
                        val uuid = Uuid.random()

                        response.respond {
                            botOutput.primaryEmbedBuilder?.let { embed(it) }

                            botOutput.buttons?.let { buttonSet ->
                                if (buttonSet.buttonList.isEmpty().not()) {
                                    createButtons(uuid, buttonSet.buttonList)

                                    if (buttonSet.duration != EMBED_BUTTON_DURATION_INF.seconds) {
                                        coroutineScope.launch {
                                            delay(buttonSet.duration)
                                            interaction.getOriginalInteractionResponse().edit {
                                                components = mutableListOf()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .onError { error ->
                        response.respond {
                            embed {
                                title = "Interaction Failed"
                                description = error.toString()
                                color = Color(0x00FF0000)
                            }
                        }
                    }
            }
            (KEY_EDIT in action) -> {
                interaction.deferPublicMessageUpdate()
                val uuid = action.substringAfter(KEY_EDIT)
                message?.apply {
                    val botOutput = editableEmbedMap[uuid]
                    botOutput?.mutableEmbedBuilder?.manualEditBuilder?.let { embedBuilder ->

                        edit {
                            embeds?.clear()
                            embed(embedBuilder)
                            editableEmbedMap.remove(uuid)
                            components = mutableListOf() //removes buttons
                            botOutput.images?.urls?.forEach { url ->
                                embed {
                                    title = botOutput.images.title
                                    this.url = botOutput.images.titleUrl
                                    image = url
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                Napier.e(tag = TAG) { "Invalid button action" }
            }
        }
    }

    private suspend fun createCommandsForTestServer() {
        val testGuildSnowFlake = Snowflake(adminConfig.adminServerId)
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
                .distinctBy {
                    it.name.lowercase() }
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
    }

    private suspend fun createAdminCommands() {
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
                Napier.e(tag = TAG) {
                    "Non-recoverable disconnect on shard $shard: code=${closeCode.code} ($closeCode)"
                }
            } else {
                Napier.d(tag = TAG) {
                    "Gateway disconnect on shard $shard: code=${closeCode.code}, will reconnect"
                }
            }
        }

        kord.on<dev.kord.core.event.gateway.ResumedEvent> {
            Napier.i(tag = TAG) { "Gateway resumed successfully" }
        }
    }


    private companion object {
        const val TAG = "DiscordBot"
    }
}
