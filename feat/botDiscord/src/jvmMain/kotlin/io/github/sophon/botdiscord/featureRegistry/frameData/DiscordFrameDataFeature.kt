package io.github.sophon.botdiscord.featureRegistry.frameData

import MAX_LENGTH_EMBED
import io.github.sophon.botdiscord.BotError
import io.github.sophon.botdiscord.domain.usecase.DownloadDataUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHeatMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetHomingMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.GetPowerCrushMovesUseCase
import io.github.sophon.botdiscord.domain.usecase.SearchFrameDataUseCase
import io.github.sophon.botdiscord.featureRegistry.Command
import io.github.sophon.botdiscord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.botdiscord.featureRegistry.SlashCommand
import io.github.sophon.botdiscord.util.createErrorEmbed
import io.github.sophon.botdiscord.util.field
import io.github.sophon.botdiscord.util.orClickable
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.truncate
import io.github.sophon.wikiwavu.domain.WavuUrlProvider
import io.github.sophon.wikiwavu.domain.model.Move
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.FeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.hours

internal class FrameDataFeatureDiscord(
    private val downloadDataUseCase: DownloadDataUseCase,
    private val searchFrameDataUseCase: SearchFrameDataUseCase,
    private val getPowerCrushMovesUseCase: GetPowerCrushMovesUseCase,
    private val getHeatMovesUseCase: GetHeatMovesUseCase,
    private val getHomingMovesUseCase: GetHomingMovesUseCase,
    private val urlProvider: WavuUrlProvider,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature {
    override val mainCommand: Command = Command.FD
    override val featureInfo = FeatureInfo(
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
        SlashCommand(
            name = Command.HOMING,
            description = "Tekken 8 homing moves",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
    )

    override suspend fun start() {
        scheduler.start(
            period = 1.hours,
            task = ::downloadCompleteMoveList,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        return when (command) {
            Command.FD -> searchFrameData(*args)
            Command.PC -> searchPowerCrushMoves(*args)
            Command.HEAT -> searchHeatMoves(*args)
            Command.HOMING -> searchHomingMoves(*args)
            else -> createErrorEmbed(BotError.BOT_LOGIC_ERROR)
        }
    }


    private suspend fun downloadCompleteMoveList(): EmptyResult<BotError> {
        return downloadDataUseCase.invoke()
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

    private suspend fun searchPowerCrushMoves(vararg args: String): EmbedBuilder.() -> Unit {
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

    private suspend fun searchHeatMoves(vararg args: String): EmbedBuilder.() -> Unit {
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

    private suspend fun searchHomingMoves(vararg args: String): EmbedBuilder.() -> Unit {
        val result = getHomingMovesUseCase.invoke(
            charName = args.joinToString(" ")
        )
        return when (result) {
            is Result.Success -> {
                createMoveListEmbed(category = "Homing", moves = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }

    private fun createMoveEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = move.input
        url = urlProvider.charUrl(move.charName)

        description = "**${move.charName}**: ${move.name}"
        color = Color(GREEN)

        field(name = "SU", value = move.startup)
        field(name = "OH", value = move.onHit.orClickable().orDash())
        field(name = "OB", value = move.onBlock.orDash())
        field(name = "CH", value = (move.onCH ?: move.onHit).orClickable().orDash())
        field(name = "LVL", value = move.level)
        move.recoveryOnWhiff
            ?.takeIf { it.isNotEmpty() }
            ?.let { field(name = "Recovery", value = it) }

        field(name = "DMG", value = move.damage.orDash())

        createNotes(move)

        urlProvider.videoUrl(move)?.let { url ->
            field(name = "Video", value = "[Link](${url})")
        }

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun EmbedBuilder.createNotes(move: Move) {
        val aliasNote = if (move.aliases.isNotEmpty()) {
            "Alt inputs: ${move.aliases.joinToString("; ")}"
        } else null

        val allNotes = buildList {
            addAll(move.notes.mapNotNull { it.orClickable() })
            aliasNote?.let { add(it) }
        }

        return field(
            name = "📝 NOTES",
            value = allNotes
                .emojify(crushes = move.crushes)
                .joinToString(separator = "") { note -> "* $note\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun createMoveListEmbed(
        category: String,
        moves: List<Move>
    ): EmbedBuilder.() -> Unit = {
        field(
            name = "$category moves".uppercase(),
            value = moves
                .joinToString(separator = "") { move -> "* ${move.input}\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun List<String>.emojify(
        crushes: List<String>,
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
                    if (note.contains("Throw", ignoreCase = true)) append("️🤝 ")
                    append(note)
                }
                add(emojified)
            }

            crushes
                .filter { it.contains("pc", ignoreCase = true) }
                .forEach { crush ->
                    add("🛡️ $crush")
                }
        }
    }
}


private const val TAG = "FrameDataFeature"
private const val KEY_CHAR_NAME = "character"
private const val KEY_MOVE = "move"
private const val GREEN = 0x00FF00