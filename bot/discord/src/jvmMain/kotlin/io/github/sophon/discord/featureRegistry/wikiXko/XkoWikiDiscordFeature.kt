package io.github.sophon.discord.featureRegistry.wikiXko

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.BotError
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import io.github.sophon.xko.domain.XkoFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class XkoWikiDiscordFeature(
    xkoFeatureInfo: XkoFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = xkoFeatureInfo.featureInfo
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
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.FDXKO,
            description = "2XKO frame data",
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
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.Xko.id)) {
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

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        val wiki = wikis[Game.Xko.id]
            ?: return Result.Error(BotError.UnsupportedGame(query))

        return when (command) {
            Command.FD,
            Command.FDXKO,
                -> searchMove(wiki, query)

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
            .map { move ->
                BotOutput(
                    embedBuilder = createMoveEmbed(move),
                    images = if (move.urls.hitboxImageList.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = move.urls.hitboxImageList,
                        )
                    }
                )
            }
    }

    private fun createMoveEmbed(move: Move): EmbedBuilder.() -> Unit = {
        title = "${move.charName}: ${move.input}"

        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: move.urls.moveImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()
        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }

        color = Color(GREEN)

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Active", value = move.active.orDash())

        optionalField(name = "Recovery", value = move.recovery)
        optionalField(name = "Damage", value = move.damage)

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }


    private companion object {
        private const val TAG = "XkoWikiDiscordFeature"
        private const val KEY_CHAR_NAME = "character"
        private const val KEY_MOVE = "move"
        private const val GREEN = 0xCDF564
    }
}