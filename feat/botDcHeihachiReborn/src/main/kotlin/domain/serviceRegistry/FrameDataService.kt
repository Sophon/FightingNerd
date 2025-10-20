package domain.serviceRegistry

import BotError
import MAX_LENGTH_EMBED
import UrlProvider
import com.example.core.domain.Result
import com.example.core.util.truncate
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import model.Move
import usecase.GetHeatMovesUseCase
import usecase.GetPowerCrushMovesUseCase
import usecase.SearchFrameDataUseCase
import usecase.StartWikiUseCase
import util.createErrorEmbed
import util.field

internal class FrameDataService(
    private val startWikiUseCase: StartWikiUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val getPowerCrushMovesUseCase: GetPowerCrushMovesUseCase,
    private val getHeatMovesUseCase: GetHeatMovesUseCase,
    private val urlProvider: UrlProvider,
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
        ),
        SlashCommand(
            name = Command.PC,
            description = "Tekken 8 Power Crush moves",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
        SlashCommand(
            name = Command.HEAT,
            description = "Tekken 8 Power Crush moves",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
    )

    override suspend fun start() {
        startWikiUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        return when (command) {
            Command.FD -> searchFrameData(*args)
            Command.PC -> searchPowerCrushMoves(*args)
            Command.HEAT -> searchHeatMoves(*args)
            else -> createErrorEmbed(BotError.BOT_LOGIC_ERROR)
        }
    }

    private suspend fun searchFrameData(
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        val result = searchFrameDataUseCase.invoke(
            query = args.joinToString(" ")
        )
        return when (result) {
            is Result.Success -> {
                createMoveEmbed(move = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }

    private suspend fun searchPowerCrushMoves(vararg  args: String): EmbedBuilder.() -> Unit {
        val result = getPowerCrushMovesUseCase.invoke(
            charName = args.joinToString(" ")
        )
        return when (result) {
            is Result.Success -> {
                createMoveListEmbed(category = "Power Crush", moves = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }

    private suspend fun searchHeatMoves(vararg  args: String): EmbedBuilder.() -> Unit {
        val result = getHeatMovesUseCase.invoke(
            charName = args.joinToString(" ")
        )
        return when (result) {
            is Result.Success -> {
                createMoveListEmbed(category = "Heat", moves = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }

    private fun createMoveEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = move.id //TODO: clickable

        description = move.name //TODO: clickable
        color = Color(GREEN)

        field(name = "SU", value = move.startup)
        clickableField(name = "OH", value = move.onHit.orDash())
        clickableField(
            name = "OH",
            value = move.onHit.orDash()
        )
        field(name = "OB", value = move.onBlock.orDash())
        clickableField(
            name = "CH",
            value = move.onCH ?: move.onHit.orDash()
        )
        field(name = "LVL", value = move.level)
        move.recoveryOnWhiff
            ?.takeIf { it.isNotEmpty() }
            ?.let { field(name = "Recovery", value = it) }

        field(name = "DMG", value = move.damage.orDash())

        field(
            name = "📝 NOTES",
            value = move.notes
                .emojify(crush = move.crush)
                .joinToString(separator = "") { note -> "* $note\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )

        urlProvider.videoUrl(move)?.let { url ->
            field(name = "Video", value = "[Link](${url})")
        }

        footer {
            text = serviceInfo.name
            icon = serviceInfo.iconUrl
        }
    }

    private fun createMoveListEmbed(
        category: String,
        moves: List<Move>
    ): EmbedBuilder.() -> Unit = {
        field(
            name = "$category moves".uppercase(),
            value = moves
                .joinToString(separator = "") { move -> "* ${move.id}\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun EmbedBuilder.clickableField(
        name: String,
        value: String,
    ) {
        val url = urlProvider.followUpUrl(value)
        val formattedValue = if (url == null) {
            value
        } else {
            "[${value.removeFollowups()}]($url)"
        }

        field(
            name = name,
            value = formattedValue,
        )
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

    private fun String.removeFollowups(): String? {
        return this
            .substringAfterLast("|")
            .removeSuffix("]]")
    }

    private fun String?.orDash(): String = this ?: "-"
}


private const val KEY_CHAR_NAME = "character"
private const val KEY_MOVE = "move"
private const val GREEN = 0x00FF00