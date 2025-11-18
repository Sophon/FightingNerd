package io.github.sophon.discord.featureRegistry.wikiWavu

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.Scheduler
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.usecase.GetMovesUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.discord.util.orClickable
import io.github.sophon.wikiwavu.domain.WavuFeatureInfo
import io.github.sophon.wikiwavu.domain.WavuUrlProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.hours

internal class WavuWikiDiscordFeature(
    wavuFeatureInfo: WavuFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = wavuFeatureInfo.featureInfo
    override val defaultCommand = SupportedCommand(
        command = Command.FD,
        description = "Global frame data",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_CHAR_NAME,
                description = "Character name",
            ),
            SupportedCommand.Argument(
                name = KEY_MOVE,
                description = "Move",
            )
        )
    )
    override val otherCommands: List<SupportedCommand> = listOf(
        SupportedCommand(
            command = Command.FDT8,
            description = "Tekken 8 frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move",
                )
            )
        ),
        SupportedCommand(
            command = Command.PC,
            description = "Tekken 8 Power Crush moves",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
        SupportedCommand(
            command = Command.HEAT,
            description = "Tekken 8 Power Crush moves",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
        SupportedCommand(
            command = Command.HOMING,
            description = "Tekken 8 homing moves",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
            )
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<String>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGames
        }

        supportedGames.forEach { gameId ->
            wikis[gameId] = get {
                parametersOf(
                    gameId,
                    InMemoryCharacterListDB(),
                    InMemoryMoveListDB(),
                )
            }
        }
    }

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        scheduler.start(
            period = 1.hours,
            task = ::syncData,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<BotOutput, BotError> {
        val gameId = "Tekken_8" //currently only supporting T8

        val wiki = wikis[gameId]
            ?: return Result.Error(BotError.UnsupportedGame(query))

        return when (command) {
            Command.FD,
            Command.FDT8,
                -> searchMove(wiki, query)

            Command.PC -> searchPowerCrushMoves(wiki, query)
            Command.HEAT -> searchHeatMoves(wiki, query)
            Command.HOMING -> searchHomingMoves(wiki, query)
            else -> {
                val error = BotError.BotLogicError(command.name, query)
                Result.Error(error)
            }
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { BotOutput(embedBuilder = createMoveEmbed(move = it)) }
    }

    private suspend fun searchPowerCrushMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = query,
            predicate = { it.t8Properties?.isPowerCrush == true },
        )
            .map { moveList ->
                BotOutput(
                    embedBuilder = createMoveListEmbed(
                        category = "Power Crush",
                        moves = moveList
                    )
                )
            }
    }

    private suspend fun searchHeatMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = query,
            predicate = { it.t8Properties?.isHeat == true },
        )
            .map { moveList ->
                BotOutput(
                    embedBuilder = createMoveListEmbed(
                        category = "Power Crush",
                        moves = moveList
                    )
                )
            }
    }

    private suspend fun searchHomingMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = query,
            predicate = { it.t8Properties?.isHoming == true },
        )
            .map { moveList ->
                BotOutput(
                    embedBuilder = createMoveListEmbed(
                        category = "Power Crush",
                        moves = moveList
                    )
                )
            }
    }

    private fun createMoveEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = move.input
        url = WavuUrlProvider.charUrl(move.charName)

        description = "**${move.charName}**: ${move.name}"
        color = Color(GREEN)

        mandatoryField(name = "SU", value = move.startup)
        mandatoryField(name = "OH", value = move.onHit.orClickable())
        mandatoryField(name = "OB", value = move.onBlock)
        mandatoryField(name = "CH", value = (move.onCH ?: move.onHit).orClickable())
        mandatoryField(name = "LVL", value = move.t8Properties?.level)


        optionalField(name = "Rec", value = move.recovery)
        optionalField(name = "DMG", value = move.damage)

        createNotes(move)

        optionalField(name = "Video", value = "[Link](${url})")

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

        return optionalField(
            name = "📝 NOTES",
            value = allNotes
                .emojify()
                .joinToString(separator = "") { note -> "* $note\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun createMoveListEmbed(
        category: String,
        moves: List<Move>
    ): EmbedBuilder.() -> Unit = {
        mandatoryField(
            name = "$category moves".uppercase(),
            value = moves
                .joinToString(separator = "") { move -> "* ${move.input}\n" }
                .truncate(MAX_LENGTH_EMBED),
            inline = false,
        )
    }

    private fun List<String>.emojify(): List<String> {
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
                    if (note.contains("pc", ignoreCase = true)) append("🛡️ ")
                    append(note)
                }
                add(emojified)
            }
        }
    }


    private companion object {
        private const val TAG = "WavuWikiDiscordFeature"
        private const val KEY_CHAR_NAME = "character"
        private const val KEY_MOVE = "move"
        private const val GREEN = 0x00FF00
    }
}