package io.github.sophon.discord.feat.wikiDragDown

import dev.kord.common.Color
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
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import io.github.sophon.discord.feat.core.ui.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikidragdown.integration.DragDownFeatureInfo
import io.github.sophon.wikidragdown.integration.DragDownFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.seconds

internal class DragDownWikiDiscordFeature(
    dragDownFeatureInfo: DragDownFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dragDownFeatureInfo.featureInfo
    override val defaultCommand: Command = Command.Fd
    override val otherCommands: List<Command> = listOf(
        Command.FdROA,
        Command.CharROA,
        Command.SpecialROA,
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

        val result = when(command) {
            Command.Fd -> {
                fetchMoveInWikisUseCase.invoke(
                    wikis = wikiClientMap,
                    query = formattedQuery,
                    searchFun = { _, wiki, query -> searchMove(wiki, query) },
                )
            }

            Command.CharROA -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.ROA2,
                    query = formattedQuery,
                    action = { _, wiki, query -> searchCharacter(wiki, query)},
                )
            }
            Command.FdROA -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.ROA2,
                    query = formattedQuery,
                    action = { _, wiki, query -> searchMove(wiki, query) },
                )
            }
            Command.SpecialROA -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.ROA2,
                    query = formattedQuery,
                    action = { _, wiki, query -> searchSpecialMoves(wiki, query) }
                )
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
            Command.FdROA -> Game.ROA2
            else -> return Result.Error(BotError.BotLogicError(command.name, ""))
        }
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.BotLogicError(command.name, ""))
        val result = getCharactersUseCase.invoke(wiki)
        return result
    }

    override suspend fun getMoveList(
        command: Command,
        characterId: String,
    ): Result<List<Move>, BotError> {
        val game = when (command) {
            Command.FdROA -> Game.ROA2
            else -> return Result.Error(BotError.BotLogicError(command.name, ""))
        }
        val wiki = wikiClientMap[game]
            ?: return Result.Error(BotError.BotLogicError(command.name, ""))
        val result = getMovesUseCase.invoke(characterId = characterId, wiki = wiki).map { (_, moveList) -> moveList }
        return result
    }


    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, _) ->
                BotOutput(
                    primaryEmbedBuilder = dragDownCharacterEmbed(character, featureInfo),
                )
            }
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        val result = getMoveUseCase.invoke(wiki, query)
            .map { (character, move) ->
                val images = move.urls.hitboxImageList
                    .takeIf { it.size >= 2 }
                    ?.let {
                        BotOutput.Images(
                            title = move.input,
                            titleUrl = move.urls.wikiUrl,
                            urls = it,
                        )
                    }

                BotOutput(
                    primaryEmbedBuilder = dragDownMoveEmbed(character, move, featureInfo),
                    images = images,
                )
            }
        return result
    }

    private suspend fun searchSpecialMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            characterId = query,
            filter = DragDownFilters.Specials,
        ).map { (character, moveList) ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${character.displayName.uppercase()} Specials",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(TEAL),
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = character.id),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
    }


    private companion object {
        const val TAG = "DragDownWikiDiscordFeature"
        const val TEAL = 0x002893F0 //TODO: refactor - each Discord client should have color param
    }
}