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
import io.github.sophon.discord.feat.core.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.core.util.aggregateCharacters
import io.github.sophon.discord.feat.core.util.firstMatchingWikiMoves
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
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val createMizuumiInvEmbedUseCase: CreateMizuumiInvEmbedUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
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
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            Command.FdMB -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.MBTL,
                    query = formattedQuery,
                ) { _, wiki, query ->
                    searchMove(wiki, query)
                }
            }
            Command.AliasMB -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.MBTL,
                    query = formattedQuery,
                ) { _, wiki, _ ->
                    getCharacterAliases(wiki)
                }
            }
            Command.InvMB -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.MBTL,
                    query = formattedQuery,
                ) { game, wiki, query ->
                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
                }
            }

            Command.FdUNI -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Uni2,
                    query = formattedQuery,
                ) { _, wiki, query ->
                    searchMove(wiki, query)
                }
            }
            Command.CharUNI -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Uni2,
                    query = formattedQuery,
                ) { _, wiki, query ->
                    searchCharacter(wiki, query)
                }
            }
            Command.InvUNI -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Uni2,
                    query = formattedQuery,
                ) { game, wiki, query ->
                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
                }
            }

            Command.FdVS -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.VSAV,
                    query = formattedQuery,
                ) { _, wiki, query ->
                    searchMove(wiki, query)
                }
            }
            Command.InvVS -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.VSAV,
                    query = formattedQuery,
                ) { game, wiki, query ->
                    createMizuumiInvEmbedUseCase.invoke(game, wiki, featureInfo, query)
                }
            }
            Command.AliasVS -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.VSAV,
                    query = formattedQuery,
                ) { _, wiki, _ ->
                    getCharacterAliases(wiki)
                }
            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }

        return result
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }

    override suspend fun getCharacterList(command: Command): Result<List<Character>, BotError> {
        val game = when (command) {
            Command.FdMB, Command.InvMB -> Game.MBTL
            Command.FdUNI, Command.InvUNI -> Game.Uni2
            Command.FdVS, Command.InvVS -> Game.VSAV
            else -> return Result.Error(BotError.BotLogicError(command.name, ""))
        }
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.BotLogicError(command.name, ""))
        val result = getCharactersUseCase.invoke(wiki)
        return result
    }

    override suspend fun getAllCharacters() =
        aggregateCharacters(wikiClientMap, getCharactersUseCase)

    override suspend fun getMoveList(
        command: Command,
        characterId: String,
    ): Result<List<Move>, BotError> {
        if (command == Command.Fd) {
            val moves = firstMatchingWikiMoves(wikiClientMap, getMovesUseCase, characterId)
            return moves
        }
        val game = when (command) {
            Command.FdMB, Command.InvMB -> Game.MBTL
            Command.FdUNI, Command.InvUNI -> Game.Uni2
            Command.FdVS, Command.InvVS -> Game.VSAV
            else -> return Result.Error(BotError.BotLogicError(command.name, ""))
        }
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.BotLogicError(command.name, ""))
        val result = getMovesUseCase.invoke(characterId = characterId, wiki = wiki).map { (_, moveList) -> moveList }
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