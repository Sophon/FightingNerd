import com.example.core.domain.Result
import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import com.example.core.util.isAtLeast
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.embed
import domain.EmbedBuilder
import domain.serviceRegistry.Command
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import usecase.SearchFrameDataUseCase
import usecase.SearchGlossaryUseCase
import usecase.StartGlossaryUseCase
import usecase.StartWikiUseCase
import util.removeTag
import kotlin.time.ExperimentalTime

interface HeihachiReborn {
    suspend fun startSession()
}

internal class HeihachiRebornImpl(
    private val apiKey: String,
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val startWikiUseCase: StartWikiUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val embedBuilder: EmbedBuilder,
): HeihachiReborn {
    private lateinit var kord: Kord


    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        coroutineScope {
            launch { startGlossaryUseCase.invoke() }
            launch { startWikiUseCase.invoke() }
        }
        startKord()
    }


    private suspend fun startKord() {
        kord = Kord(token = apiKey)

        createCommandsForTestServer()
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

    @OptIn(ExperimentalTime::class)
    private suspend fun MessageCreateEvent.handleMessage() {
        if (kord.selfId !in message.mentionedUserIds) return

        val query = message.content.removeTag().takeIf { it.isAtLeast(2) } ?: return
        val command = query.substringBefore(' ')

        //either a command or frame-data query
        when (command.uppercase()) {
            Command.GL.name -> {
                handleResult(searchGlossaryUseCase.invoke(query)) { glossaryItem ->
                    embedBuilder.glossaryEmbed(glossaryItem)
                }
            }
            else -> {
                handleResult(searchFrameDataUseCase.invoke(query)) { move ->
                    embedBuilder.moveEmbed(move)
                }
            }
        }
    }

    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        when (interaction.command.rootName.uppercase()) {
            Command.GL.name -> {
                val query = interaction.command.strings["term"] ?: return

                searchGlossaryUseCase.invoke(query = query)
                    .onSuccess { glossaryItem ->
                        interaction.respondPublic { embed(embedBuilder.glossaryEmbed(glossaryItem)) }
                    }
                    .onError { error ->
                        interaction.respondPublic { embed(embedBuilder.errorEmbed(error)) }
                    }
            }
            Command.FD.name -> {
                val character = interaction.command.strings["character"]
                val move = interaction.command.strings["move"]

                searchFrameDataUseCase.invoke("$character $move")
                    .onSuccess { move ->
                        interaction.respondPublic { embed(embedBuilder.moveEmbed(move)) }
                    }
                    .onError { error ->
                        interaction.respondPublic { embed(embedBuilder.errorEmbed(error)) }
                    }
            }
        }
    }

    private suspend fun <T, E: BotError> MessageCreateEvent.handleResult(
        result: Result<T, E>,
        createEmbed: (T) -> dev.kord.rest.builder.message.EmbedBuilder.() -> Unit,
    ) {
        when (result) {
            is Result.Success -> message.channel.createEmbed(createEmbed(result.data))
            is Result.Error -> message.channel.createEmbed(embedBuilder.errorEmbed(result.error))
        }
    }

    private suspend fun createCommandsForTestServer() {
        val guildId = Snowflake(TEST_SERVER_ID)
        kord.createGuildChatInputCommand(
            guildId = guildId,
            name = Command.FD.name.lowercase(),
            description = "frame data"
        ) {
            string("character", "Character name") { required = true }
            string("move", "Move input") { required = true }
        }

        kord.createGuildChatInputCommand(
            guildId = guildId,
            name = Command.GL.name.lowercase(),
            description = "frame data"
        ) {
            string("term", "Term") { required = true }
        }
    }

    private suspend fun creatGlobalCommand() {
        kord.createGlobalChatInputCommand("fd", "frame data")
    }
}

private const val TAG = "HeihachiRebornBot"