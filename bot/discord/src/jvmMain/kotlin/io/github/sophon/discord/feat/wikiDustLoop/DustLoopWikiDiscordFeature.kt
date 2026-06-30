package io.github.sophon.discord.feat.wikiDustLoop

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
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
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
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
    private var wikiClientMap: Map<Game, WikiClient> = emptyMap()

    override fun registerWikiClients(wikiClientMap: Map<Game, WikiClient>) {
        this.wikiClientMap = wikiClientMap
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
        val formattedQuery = query.lowercase()

        val result = when (command) {
            Command.Fd -> {
                fetchMoveInWikisUseCase.invoke(
                    wikis = wikiClientMap,
                    query = formattedQuery,
                    searchFun = ::searchMove,
                )
            }

            Command.CharGG -> withWiki(
                wikis = wikiClientMap,
                game = Game.GGST,
                query = formattedQuery,
                action = ::searchCharacter,
            )
            Command.FdGG -> withWiki(
                wikis = wikiClientMap,
                game = Game.GGST,
                query = formattedQuery,
                action = ::searchMove,
            )
            Command.InvGG -> withWiki(
                wikis = wikiClientMap,
                game = Game.GGST,
                query = formattedQuery,
                action = ::searchInvincible,
            )
            Command.AliasGG -> withWiki(
                wikis = wikiClientMap,
                game = Game.GGST,
                query = formattedQuery,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }

            Command.CharDB -> withWiki(
                wikis = wikiClientMap,
                game = Game.DBFZ,
                query = formattedQuery,
                action = ::searchCharacter,
            )
            Command.FdDB -> withWiki(
                wikis = wikiClientMap,
                game = Game.DBFZ,
                query = formattedQuery,
                action = ::searchMove,
            )
            Command.AliasDB -> withWiki(
                wikis = wikiClientMap,
                game = Game.DBFZ,
                query = formattedQuery,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }

            Command.CharGB -> withWiki(
                wikis = wikiClientMap,
                game = Game.GBVSR,
                query = formattedQuery,
                action = ::searchCharacter,
            )
            Command.FdGB -> withWiki(
                wikis = wikiClientMap,
                game = Game.GBVSR,
                query = formattedQuery,
                action = ::searchMove,
            )

            Command.CharBB -> withWiki(
                wikis = wikiClientMap,
                game = Game.BBCF,
                query = formattedQuery,
                action = ::searchCharacter,
            )
            Command.FdBB -> withWiki(
                wikis = wikiClientMap,
                game = Game.BBCF,
                query = formattedQuery,
                action = ::searchMove,
            )
            Command.AliasBB -> withWiki(
                wikis = wikiClientMap,
                game = Game.BBCF,
                query = formattedQuery,
            ) { _, wiki, _ ->
                getCharacterAliases(wiki)
            }
            Command.InvBB -> withWiki(
                wikis = wikiClientMap,
                game = Game.BBCF,
                query = formattedQuery,
                action = ::searchInvincible,
            )

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }

        return result
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }

    private suspend fun searchCharacter(
        game: Game,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder = createCharacterEmbedUseCase.invoke(
                        game = game,
                        character = character,
                        fastestMoveList = fastestMoveList,
                        featureInfo = featureInfo,
                    )
                )
            }
    }

    private suspend fun searchMove(
        game: Game,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        val result = getMoveUseCase.invoke(wiki, query)
            .map { (character, move) ->
                createMoveEmbedUseCase.invoke(game, character ,move, featureInfo)
            }
        return result
    }

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(wiki, featureInfo, RED)
            .map { embedBuilder ->
                BotOutput(primaryEmbedBuilder = embedBuilder)
            }
    }

    private suspend fun searchInvincible(
        game: Game,
        wiki: WikiClient,
        charName: String,
    ): Result<BotOutput, BotError> {
        return fetchDustLoopInvincibleMovesUseCase.invoke(game, wiki, charName)
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