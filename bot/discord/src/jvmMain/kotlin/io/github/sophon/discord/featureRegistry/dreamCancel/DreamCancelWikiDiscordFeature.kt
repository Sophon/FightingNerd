package io.github.sophon.discord.featureRegistry.dreamCancel

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
import io.github.sophon.core.util.getGame
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.Scheduler
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.dreamcancel.domain.DreamCancelFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class DreamCancelWikiDiscordFeature(
    dreamCancelFeatureInfo: DreamCancelFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dreamCancelFeatureInfo.featureInfo
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
            command = Command.FDKOF15,
            description = "SF6 frame data",
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
        ),
        SupportedCommand(
            command = Command.FDCOTW,
            description = "COTW frame data",
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
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.DreamCancel.id)) {
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

    //TODO: this should definitely be a usecase
    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((gameId, wiki) in wikis) {
                    when (val result = searchMove(gameId, wiki, query)) {
                        is Result.Success -> return result
                        is Result.Error -> lastError = result.error
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.FDKOF15 -> {
                val gameId = Game.KoFXV.id
                val wiki = wikis[gameId]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(gameId, wiki, query)
            }
            Command.FDCOTW -> {
                val gameId = Game.COTW.id
                val wiki = wikis[gameId]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(gameId, wiki, query)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchMove(
        gameId: String,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { BotOutput(embedBuilder = createMoveEmbed(gameId, it)) }
    }

    private fun createMoveEmbed(
        gameId: String,
        move: Move
    ): EmbedBuilder.() -> Unit = {
        title = move.input
        description = "**${move.charName}**: ${move.name}"
        color = Color(BLUE)

        gameId.getGame()?.iconUrl?.let {
            thumbnail { url = it }
        }

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
        optionalField(name = "Invul", value = move.invulnerability)
        optionalField(name = "Stun", value = move.koF15Properties?.stun)
        optionalField(name = "Rev dmg", value = move.cotwProperties?.revDamage)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }


    private companion object {
        const val TAG = "DreamCancelWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val BLUE = 0x009AB3F6
    }
}