package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
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
import io.github.sophon.discord.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import io.github.sophon.wikimizuumi.MizuumiFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class MizuumiWikiDiscordFeature(
    mizuumiFeatureInfo: MizuumiFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = mizuumiFeatureInfo.featureInfo
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
            command = Command.FDMB,
            description = "MBTL frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            ),
        ),
        SupportedCommand(
            command = Command.ALIASMB,
            description = "MBTL character aliases",
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.Mizuumi.id)) {
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
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((_, wiki) in wikis) {
                    when (val result = searchMove(wiki, query)) {
                        is Result.Success -> return result
                        is Result.Error -> lastError = result.error
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }
            Command.FDMB -> {
                val gameId = Game.MBTL.id
                val wiki = wikis[gameId]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.ALIASMB -> {
                val gameId = Game.MBTL.id
                val wiki = wikis[gameId]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
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
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: move.urls.moveImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    embedBuilder = createMoveEmbed(move),
                    images = if (images.size < 2) {
                        null
                    } else {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = images,
                        )
                    }
                )
            }
    }

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(wiki, featureInfo, TEAL)
            .map { BotOutput(embedBuilder = it) }
    }

    private fun createMoveEmbed(
        move: Move,
    ): EmbedBuilder.() -> Unit = {
        title = move.input
        url = move.urls.wikiUrl
        description = if (move.name.isNullOrBlank()) {
            "**${move.charName}**"
        } else {
            "**${move.charName}**: ${move.name.orEmpty()}"
        }
        color = Color(TEAL)

        val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
            ?: move.urls.moveImageList.takeIf { it.isNotEmpty() }
            ?: emptyList()

        images
            .takeIf { it.size == 1 }
            ?.let { image = it.first() }

        move.urls.characterImage?.let {
            thumbnail { url = it }
        }

        mandatoryField(name = "Startup", value = move.startup)
        mandatoryField(name = "Cancel", value = move.cancel)
        mandatoryField(name = "Block", value = move.onBlock)
        mandatoryField(name = "Active", value = move.active)
        mandatoryField(name = "Guard", value = move.guard)
        mandatoryField(name = "Recovery", value = move.recovery)

        optionalField(name = "Attribute", value = move.mbProperties?.attribute)
        optionalField(name = "Property", value = move.mbProperties?.property)
        optionalField(name = "Damage", value = move.damage)
        optionalField(name = "Invul", value = move.invulnerability)
        optionalField(name = "Cost", value = move.mbProperties?.cost)

        featureFooter(featureInfo)
    }


    private companion object {
        const val TAG = "MizuumiWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val TEAL = 0x0007A9F5
    }
}