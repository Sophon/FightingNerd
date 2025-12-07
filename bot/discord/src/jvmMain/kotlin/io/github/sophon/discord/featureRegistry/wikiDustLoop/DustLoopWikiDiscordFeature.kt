package io.github.sophon.discord.featureRegistry.wikiDustLoop

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
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
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.wikidustloop.domain.DustLoopFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class DustLoopWikiDiscordFeature(
    dustLoopFeatureInfo: DustLoopFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dustLoopFeatureInfo.featureInfo
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
                description = "Move input"
            )
        )
    )
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.CHARGGST,
            description = "GGST character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            )
        )
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }
        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.DustLoop.id)) {
                parametersOf(
                    game.id,
                    InMemoryCharacterListDB(),
                    InMemoryMoveListDB(),
                )
            }
        }
    }

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        scheduler.start(
            task = ::syncData,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    //TODO: extract to usecase
    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((gameId, wiki) in wikis) {
                    when (val result = searchMove(wiki, query)) {
                        is Result.Success -> return result
                        is Result.Error -> lastError = result.error
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.CHARGGST -> {
                val wiki = wikis[Game.GGST.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, _) ->
                BotOutput(embedBuilder = createCharacterEmbed(character))
            }
    }

    private fun createCharacterEmbed(
        character: Character,
    ): EmbedBuilder.() -> Unit = {
        title = character.displayName
        url = character.wikiUrl //TODO: should link to the char page
        color = Color(RED)

        character.images?.iconUrl?.let { iconUrl ->
            thumbnail { url = iconUrl }
        }

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { BotOutput(embedBuilder = createMoveEmbed(it)) }
    }

    private fun createMoveEmbed(
        move: Move,
    ): EmbedBuilder.() -> Unit = {
        title = move.input
        description = "**${move.charName}**: ${move.name.orEmpty()}"
        color = Color(RED)

        move.urls.hitboxImage?.let {
            image = it
        }

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Hit", value = move.onHit)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Damage", value = move.damage)
        optionalField(name = "Invulnerability", value = move.invulnerability)
        optionalField(name = "Counter", value = move.onCH)

        optionalField(name = "Level", value = move.airDashProperties?.level)
        optionalField(name = "Risc gain", value = move.airDashProperties?.riscGain)
        optionalField(name = "Risc loss", value = move.airDashProperties?.riscLoss)
        optionalField(name = "Cancel", value = move.airDashProperties?.cancel)
        optionalField(name = "Prorate", value = move.airDashProperties?.prorate)
        optionalField(name = "Input tension", value = move.airDashProperties?.inputTension)
        optionalField(name = "Chip", value = move.airDashProperties?.chipRatio)

        createNotes(move)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun EmbedBuilder.createNotes(move: Move) = optionalField(
        name = "📝 NOTES",
        value = move.notes
            .joinToString(separator = "") { note -> "* $note\n" }
            .truncate(MAX_LENGTH_EMBED),
        inline = false,
    )


    private companion object {
        const val TAG = "DustLoopWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val RED = 0x00950117
    }
}