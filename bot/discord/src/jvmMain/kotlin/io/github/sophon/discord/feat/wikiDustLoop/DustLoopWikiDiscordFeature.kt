package io.github.sophon.discord.feat.wikiDustLoop

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
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
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateCharacterEmbedUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateMoveEmbedUseCase
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
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
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val createMoveEmbedUseCase: CreateMoveEmbedUseCase,
    private val createCharacterEmbedUseCase: CreateCharacterEmbedUseCase,
    private val fetchDustLoopInvincibleMovesUseCase: FetchDustLoopInvincibleMovesUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dustLoopFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.CharGG,
        Command.FdGG,
        Command.InvGG,
        Command.AliasGG,

        Command.CharDB,
        Command.FdDB,
        Command.AliasDB,

        Command.CharBB,
        Command.FdBB,
        Command.AliasBB,
        Command.InvBB,

        Command.CharGB,
        Command.FdGB,
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
                fetchMoveInWikisUseCase.invoke(
                    wikis = wikis,
                    query = query,
                    searchFun = ::searchMove,
                )
            }

            Command.CharGG -> withWiki(
                wikis = wikis,
                gameId = Game.GGST.id,
                query = query,
                action = ::searchCharacter,
            )
            Command.FdGG -> withWiki(
                wikis = wikis,
                gameId = Game.GGST.id,
                query = query,
                action = ::searchMove,
            )
            Command.InvGG -> withWiki(
                wikis = wikis,
                gameId = Game.GGST.id,
                query = query,
                action = ::searchInvincible,
            )
            Command.AliasGG -> withWiki(
                wikis = wikis,
                gameId = Game.GGST.id,
                query = query,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }

            Command.CharDB -> withWiki(
                wikis = wikis,
                gameId = Game.DBFZ.id,
                query = query,
                action = ::searchCharacter,
            )
            Command.FdDB -> withWiki(
                wikis = wikis,
                gameId = Game.DBFZ.id,
                query = query,
                action = ::searchMove,
            )
            Command.AliasDB -> withWiki(
                wikis = wikis,
                gameId = Game.DBFZ.id,
                query = query,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }

            Command.CharGB -> withWiki(
                wikis = wikis,
                gameId = Game.GBVSR.id,
                query = query,
                action = ::searchCharacter,
            )
            Command.FdGB -> withWiki(
                wikis = wikis,
                gameId = Game.GBVSR.id,
                query = query,
                action = ::searchMove,
            )

            Command.CharBB -> withWiki(
                wikis = wikis,
                gameId = Game.BBCF.id,
                query = query,
                action = ::searchCharacter,
            )
            Command.FdBB -> withWiki(
                wikis = wikis,
                gameId = Game.BBCF.id,
                query = query,
                action = ::searchMove,
            )
            Command.AliasBB -> withWiki(
                wikis = wikis,
                gameId = Game.BBCF.id,
                query = query,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }
            Command.InvBB -> withWiki(
                wikis = wikis,
                gameId = Game.BBCF.id,
                query = query,
                action = ::searchInvincible,
            )

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

    private suspend fun searchCharacter(
        gameId: String,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder = createCharacterEmbedUseCase.invoke(
                        gameId = gameId,
                        character = character,
                        fastestMoveList = fastestMoveList,
                        featureInfo = featureInfo,
                    )
                )
            }
    }

    private suspend fun searchMove(
        gameId: String,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                createMoveEmbedUseCase.invoke(gameId, move, featureInfo)
            }
    }

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(wiki, featureInfo, RED)
            .map { embedBuilder ->
                BotOutput(primaryEmbedBuilder = embedBuilder)
            }
    }

    private suspend fun searchInvincible(
        gameId: String,
        wiki: WikiClient,
        charName: String,
    ): Result<BotOutput, BotError> {
        return fetchDustLoopInvincibleMovesUseCase.invoke(gameId, wiki, charName)
            .map { moveList ->
                BotOutput(
                    primaryEmbedBuilder = dustLoopMoveListEmbedBuilder(
                        charName = charName,
                        category = "invincible",
                        moveList = moveList,
                        featureInfo = featureInfo,
                    ),
                )
            }
    }


    private companion object {
        const val TAG = "DustLoopWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val RED = 0x00950117
    }
}