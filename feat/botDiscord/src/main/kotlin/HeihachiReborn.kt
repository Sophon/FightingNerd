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
import domain.serviceRegistry.Command
import domain.serviceRegistry.FrameDataService
import domain.serviceRegistry.GlossaryService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import util.removeTag
import kotlin.time.ExperimentalTime

interface HeihachiReborn {
    suspend fun startSession()
}

internal class HeihachiRebornImpl(
    private val apiKey: String,
    frameDataService: FrameDataService,
    glossaryService: GlossaryService,
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
                launch { service.start() }
            }
        }
        startKord()
    }


    private suspend fun startKord() {
        kord = Kord(token = apiKey)

        createGlobalCommand()
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

    @OptIn(ExperimentalTime::class)
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
        val service = services.first { service ->
            service.slashCommands.any { it.name == command }
        }

        message.channel.createEmbed(
            service.execute(command, query)
        )
    }

    private suspend fun GuildChatInputCommandInteractionCreateEvent.handleCommand() {
        val command = Command.entries
            .find { it.name.equals(interaction.command.rootName, ignoreCase = true) }
            ?: return //this should NEVER happen
        val service = services.first { service ->
            service.slashCommands.any { it.name == command }
        }
        val args = interaction.command.strings
        val query = service.buildQuery(args, command)
        val embedBuilder = service.execute(command, query)

        interaction.respondPublic { embed(embedBuilder) }
    }

    private suspend fun createCommandsForTestServer() {
        val testGuildId = Snowflake(TEST_SERVER_ID)

        services.forEach { service ->
            service.slashCommands.forEach { slashCommand ->
                kord.createGuildChatInputCommand(
                    guildId = testGuildId,
                    name = slashCommand.name.name.lowercase(),
                    description = slashCommand.description,
                ) {
                    slashCommand.arguments.forEach { argument ->
                        string(name = argument.name, description = argument.description) {
                            required = argument.isRequired
                        }
                    }
                }
            }
        }
    }

    private suspend fun createGlobalCommand() {
        services.forEach { service ->
            service.slashCommands.forEach { slashCommand ->
                kord.createGlobalChatInputCommand(
                    name = slashCommand.name.name.lowercase(),
                    description = slashCommand.description,
                ) {
                    slashCommand.arguments.forEach { argument ->
                        string(name = argument.name, description = argument.description) {
                            required = argument.isRequired
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "HeihachiRebornBot"