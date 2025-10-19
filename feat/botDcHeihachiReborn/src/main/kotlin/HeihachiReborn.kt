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
import domain.serviceRegistry.FrameDataService
import domain.serviceRegistry.GlossaryService
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
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val embedBuilder: EmbedBuilder,

    private val frameDataService: FrameDataService,
    private val glossaryService: GlossaryService,
): HeihachiReborn {
    private lateinit var kord: Kord
    private val services = listOf(
        frameDataService,
        glossaryService,
    )

    override suspend fun startSession() {
        Napier.d(tag = TAG) { "Starting with API: $apiKey" }

        coroutineScope {
            services.forEach { service ->
                service.start()
            }
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

        val rawQuery = message.content.removeTag().takeIf { it.isAtLeast(wordCount = 2) } ?: return
        val firstWord = rawQuery.substringBefore(' ')
        val command = Command.entries.find { it.name.equals(firstWord, ignoreCase = true) }
            ?: Command.FD
        val query = if (command == Command.FD) {
            // "kaz 112" -> query = "kaz 112"
            firstWord.lowercase() + rawQuery.substring(firstWord.length)
        } else {
            // "gl term" -> command = GL, query = "term"
            rawQuery.substringAfter(' ', rawQuery)
        }
        val service = services.first { it.command == command }

        message.channel.createEmbed(
            service.execute(command, query)
        )
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