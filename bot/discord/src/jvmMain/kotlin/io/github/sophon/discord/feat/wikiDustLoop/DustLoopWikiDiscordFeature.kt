package io.github.sophon.discord.feat.wikiDustLoop

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import io.github.sophon.discord.feat.core.usecase.CreateAliasOutputUseCase
import io.github.sophon.discord.feat.core.usecase.FetchCharacterInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateCharacterEmbedUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.CreateMoveEmbedUseCase
import io.github.sophon.discord.feat.wikiDustLoop.usecase.FetchDustLoopInvincibleMovesUseCase
import io.github.sophon.discord.util.aggregateCharacters
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
    private val createMoveEmbedUseCase: CreateMoveEmbedUseCase,
    private val createCharacterEmbedUseCase: CreateCharacterEmbedUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val fetchCharacterInWikisUseCase: FetchCharacterInWikisUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val createAliasOutputUseCase: CreateAliasOutputUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dustLoopFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.Alias,
        Command.Char,
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
        game: Game?,
    ): Result<BotOutput, BotError> {
        val formattedQuery = query.lowercase()

        val result = when (command) {
            Command.Fd -> {
                if (game == null) {
                    fetchMoveInWikisUseCase(
                        wikis = wikiClientMap,
                        query = formattedQuery,
                        searchFun = ::searchMove,
                    )
                } else {
                    withWiki(
                        wikis = wikiClientMap,
                        game = game,
                        query = formattedQuery,
                        action = ::searchMove,
                    )
                }
            }

            Command.Alias -> {
                createAliasOutputUseCase(gameId = query)
            }

            Command.Char -> {
                if (game == null) {
                    fetchCharacterInWikisUseCase(
                        wikis = wikiClientMap,
                        query = formattedQuery,
                        searchFun = ::searchCharacter,
                    )
                } else {
                    withWiki(
                        wikis = wikiClientMap,
                        game = game,
                        query = formattedQuery,
                        action = ::searchCharacter,
                    )
                }
            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }

        return result
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase(wikiList = wikiClientMap.values)
    }

    override suspend fun getCharacterList(game: Game): Result<List<Character>, BotError> {
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.UnsupportedGame(game.displayName))
        val result = getCharactersUseCase(wiki)
        return result
    }

    override suspend fun getMoveList(
        game: Game,
        characterId: String,
    ): Result<List<Move>, BotError> {
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.UnsupportedGame(game.displayName))
        val result = getMovesUseCase(characterQuery = characterId, wiki = wiki)
            .map { (_, moveList) -> moveList }
        return result
    }

    override suspend fun getAllCharacters(): Result<List<Pair<Game, Character>>, BotError> {
        val result = aggregateCharacters(wikiClientMap, getCharactersUseCase)
        return result
    }

    override suspend fun getList(
        command: Command,
        characterId: String,
    ): Result<List<String>, BotError> {
        return Result.Error(BotError.BotLogicError(command.name, characterId))
    }


    private suspend fun searchCharacter(
        game: Game,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase(wiki = wiki, charName = query)
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
        val result = getMoveUseCase(wiki, query)
            .map { (character, move) ->
                createMoveEmbedUseCase.invoke(game, character ,move, featureInfo)
            }
        return result
    }


    private companion object {
        const val TAG = "DustLoopWikiDiscordFeature"
    }
}