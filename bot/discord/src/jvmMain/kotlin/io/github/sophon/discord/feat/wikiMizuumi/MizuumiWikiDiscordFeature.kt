package io.github.sophon.discord.feat.wikiMizuumi

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
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
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.aggregateCharacters
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent

internal class MizuumiWikiDiscordFeature(
    mizuumiFeatureInfo: MizuumiFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val createMizuumiInvEmbedUseCase: CreateMizuumiInvEmbedUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val createAliasOutputUseCase: CreateAliasOutputUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo = mizuumiFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.Alias,
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
                if (game != null) {
                    withWiki(
                        wikis = wikiClientMap,
                        game = game,
                        query = formattedQuery,
                    ) { _, wiki, query -> searchMove(wiki, query) }
                } else {
                    fetchMoveInWikisUseCase.invoke(
                        wikis = wikiClientMap,
                        query = formattedQuery,
                    ) { _, wiki, query -> searchMove(wiki, query) }
                }
            }

            Command.Alias -> {
                createAliasOutputUseCase.invoke(gameId = query)
            }

            Command.Char -> {
//                withWiki(
//                    wikis = wikiClientMap,
//                    game = game,
//                    query = formattedQuery,
//                ) { _, wiki, query ->
//                    searchCharacter(wiki, query)
//                }

                TODO()
            }

//            Command.InvMB -> {
//                withWiki(
//                    wikis = wikiClientMap,
//                    game = Game.MBTL,
//                    query = formattedQuery,
//                ) { game, wiki, query ->
//                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
//                }
//            }
//            Command.InvUNI -> {
//                withWiki(
//                    wikis = wikiClientMap,
//                    game = Game.Uni2,
//                    query = formattedQuery,
//                ) { game, wiki, query ->
//                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
//                }
//            }
//            Command.InvVS -> {
//                withWiki(
//                    wikis = wikiClientMap,
//                    game = Game.VSAV,
//                    query = formattedQuery,
//                ) { game, wiki, query ->
//                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
//                }
//            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }

        return result
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }

    override suspend fun getCharacterList(game: Game): Result<List<Character>, BotError> {
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.UnsupportedGame(game.displayName))
        val result = getCharactersUseCase.invoke(wiki)
        return result
    }

    override suspend fun getAllCharacters(): Result<List<Pair<Game, Character>>, BotError> {
        val result = aggregateCharacters(wikiClientMap, getCharactersUseCase)
        return result
    }

    override suspend fun getMoveList(
        game: Game,
        characterId: String,
    ): Result<List<Move>, BotError> {
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.UnsupportedGame(game.displayName))
        val result = getMovesUseCase.invoke(characterQuery = characterId, wiki = wiki)
            .map { (_, moveList) -> moveList }
        return result
    }


    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { (character, move) ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    primaryEmbedBuilder = mizuumiMoveEmbed(character, move, featureInfo),
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


    private companion object {
        const val TAG = "MizuumiWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val TEAL = 0x0007A9F5
    }
}