package domain.serviceRegistry

import MAX_LENGTH_EMBED
import com.example.core.domain.Result
import com.example.core.util.truncate
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import model.Move
import usecase.SearchFrameDataUseCase
import usecase.StartWikiUseCase
import util.createErrorEmbed
import util.field

internal class FrameDataService(
    private val startWikiUseCase: StartWikiUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
): RegisteredService {
    override val mainCommand: Command = Command.FD
    override val serviceInfo = ServiceInfo(
        name = "Wavu Wiki",
        url = "https://wavu.wiki/",
        iconUrl = "https://i.imgur.com/0cnTzNk.png",
    )
    override val slashCommands: List<SlashCommand> = listOf(
        SlashCommand(
            name = Command.FD,
            description = "Tekken 8 frame data",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SlashCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move",
                )
            )
        )
    )

    override suspend fun start() {
        startWikiUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        val result = searchFrameDataUseCase.invoke(
            query = args.joinToString(" ")
        )
        return when (result) {
            is Result.Success -> {
                createEmbed(move = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }

    private fun createEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = move.characterName //TODO: clickable
        description = "${move.id} - ${move.name}" //TODO: clickable
        color = Color(GREEN)

        field(name = "Startup", value = move.startup)
        field(name = "OH", value = move.onHit.removeFollowups() ?: "-")
        field(name = "OB", value = move.onBlock)
        field(name = "CH", value = move.onCH.removeFollowups() ?: move.onHit ?: "-")
        field(name = "Level", value = move.level)
        move.recoveryOnWhiff
            ?.takeIf { it.isNotEmpty() }
            ?.let { field(name = "Recovery", value = it) }

        field(name = "Damage", value = move.damage.orEmpty(),)

        field(
            name = "📝 NOTES",
            value = move.notes
                .emojify(crush = move.crush)
                .joinToString(separator = "") { "* $it\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )

        //TODO: feedback command
        footer {
            text = serviceInfo.name
            icon = serviceInfo.iconUrl
        }
    }

    private fun List<String>.emojify(
        crush: String?
    ): List<String> {
        return buildList {
            this@emojify.forEach { note ->
                val emojified = buildString {
                    if (note.contains("Heat", ignoreCase = true)) append("🔥 ")
                    if (note.contains("Balcony Break", ignoreCase = true)) append("➡️ ")
                    if (note.contains("Spike", ignoreCase = true)) append("⬇️ ")
                    if (note.contains("Floor break", ignoreCase = true)) append("⬇️ ")
                    if (note.contains("Tornado", ignoreCase = true)) append("🌪️ ")
                    if (note.contains("Tailspin", ignoreCase = true)) append("️🌀 ")
                    if (note.contains("Transition", ignoreCase = true)) append("️⏭️ ")
                    if (note.contains("Homing", ignoreCase = true)) append("️🔄 ")
                    append(note)
                }
                add(emojified)
            }

            crush?.takeIf { it.contains("pc", ignoreCase = true) }?.let {
                add("🛡️ $it")
            }
        }
    }

    private fun String?.removeFollowups(): String? {
        return this
            ?.substringAfterLast("|")
            ?.removeSuffix("]]")
    }
}


private const val KEY_CHAR_NAME = "character"
private const val KEY_MOVE = "move"
private const val GREEN = 0x00FF00