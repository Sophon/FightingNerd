package io.github.sophon.discord.feat.wikiMizuumi

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.feat.core.data.InMemoryCharacterListDB
import io.github.sophon.discord.feat.core.data.InMemoryMoveListDB
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.integration.model.Source
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
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
    private val getCharacterUseCase: GetCharacterUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val createMizuumiInvEmbedUseCase: CreateMizuumiInvEmbedUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo = mizuumiFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.FdMB,
        Command.AliasMB,
        Command.InvMB,
        Command.FdUNI,
        Command.CharUNI,
        Command.InvUNI,
        Command.FdVS,
        Command.InvVS,
        Command.AliasVS,
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
            task = ::refreshData,
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
            Command.Fd -> {
                var lastError: BotError? = null
                for ((gameId, wiki) in wikis) {
                    val game = Game.fromId(gameId)
                    if (game == null) {
                        Result.Error(lastError ?: BotError.UnknownMove(query))
                    } else {
                        when (val result = searchMove(wiki, query)) {
                            is Result.Success -> return result
                            is Result.Error -> lastError = result.error
                        }
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }

            Command.FdMB -> {
                val game = Game.MBTL
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.AliasMB -> {
                val game = Game.MBTL
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }
            Command.InvMB -> {
                val game = Game.MBTL
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
            }

            Command.FdUNI -> {
                val game = Game.Uni2
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.CharUNI -> {
                val game = Game.Uni2
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query)
            }
            Command.InvUNI -> {
                val game = Game.Uni2
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
            }

            Command.FdVS -> {
                val game = Game.VSAV
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query)
            }
            Command.InvVS -> {
                val game = Game.VSAV
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
            }
            Command.AliasVS -> {
                val game = Game.VSAV
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }

            else -> Result.Error(
                BotError.BotLogicError(
                    command.name,
                    query,
                )
            )
        }
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    primaryEmbedBuilder = mizuumiMoveEmbed(move, featureInfo),
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

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki, query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder = mizuumiCharacterEmbed(
                        character,
                        fastestMoveList,
                        featureInfo,
                    )
                )
            }
    }

    private suspend fun getCharacterAliases(
        wiki: WikiClient,
    ): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(
            wiki = wiki,
            featureInfo = featureInfo,
            colorCode = TEAL,
        ).map { BotOutput(primaryEmbedBuilder = it) }
    }


    private companion object {
        const val TAG = "MizuumiWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val TEAL = 0x0007A9F5
    }
}