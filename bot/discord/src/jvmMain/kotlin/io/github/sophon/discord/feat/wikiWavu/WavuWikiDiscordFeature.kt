package io.github.sophon.discord.feat.wikiWavu

import dev.kord.common.Color
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
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
import io.github.sophon.discord.feat.core.domain.model.Emoji
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import io.github.sophon.discord.feat.core.ui.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharactersUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.SearchStringFollowupsUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import kotlin.time.Duration.Companion.seconds

internal class WavuWikiDiscordFeature(
    wavuFeatureInfo: WavuFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val getStancesUseCase: GetStancesUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val searchStringFollowupsUseCase: SearchStringFollowupsUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val getCharactersUseCase: GetCharactersUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo = wavuFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.FdTK,
        Command.Pc,
        Command.Heat,
        Command.Homing,
        Command.Stance,
        Command.AliasTK,
        Command.ThrowTK,
        Command.Strings,
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

            Command.FdTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            Command.Pc -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchPowerCrushMoves(wiki, query) }
            }
            Command.Heat -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchHeatMoves(wiki, query) }
            }
            Command.Homing -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchHomingMoves(wiki, query) }
            }
            Command.ThrowTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchThrowMoves(wiki, query) }
            }

            Command.AliasTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, _ -> getCharacterAliases(wiki) }
            }

            Command.Stance -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> getStancesUseCase.invoke(featureInfo, wiki, query) }
            }
            Command.Strings -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = formattedQuery,
                ) { _, wiki, query -> searchStringFollowupsUseCase.invoke(wiki, query, featureInfo) }
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
            Command.FdTK,
            Command.Heat,
            Command.Homing,
            Command.Pc,
            Command.Stance,
            Command.Strings -> Game.Tekken8
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
            Command.FdTK,
            Command.Heat,
            Command.Homing,
            Command.Pc,
            Command.Stance,
            Command.Strings -> Game.Tekken8
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
                BotOutput(primaryEmbedBuilder = wavuMoveEmbed(character, move, featureInfo))
            }
    }

    private suspend fun searchPowerCrushMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        val result = getMovesUseCase.invoke(
            wiki = wiki,
            characterId = query,
            filter = TekkenFilters.PowerCrush,
        ).map { (character, moveList) ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${character.displayName.uppercase()} Power Crush",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                    emoji = Emoji.TK_PC,
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = character.id),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
        return result
    }

    private suspend fun searchHeatMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            characterId = query,
            filter = TekkenFilters.Heat,
        ).map { (character, moveList) ->
                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = "${character.displayName.uppercase()} Heat",
                        dataList = moveList.map { it.input },
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                        emoji = Emoji.TK_HEAT,
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = character.id),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
            }
    }

    private suspend fun searchHomingMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            characterId = query,
            filter = TekkenFilters.Homing,
        ).map { (character, moveList) ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${character.displayName.uppercase()} Homing",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                    emoji = Emoji.TK_HOMING,
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = character.id),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
    }

    private suspend fun searchThrowMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            characterId = query,
            filter = TekkenFilters.Throw,
        ).map { (character, moveList) ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${character.displayName.uppercase()} Throw",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                    emoji = Emoji.THROW,
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = character.id),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
    }

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(
            wiki = wiki,
            featureInfo = featureInfo,
            colorCode = BLUE,
        )
            .map { BotOutput(primaryEmbedBuilder = it) }
    }


    private companion object {
        private const val TAG = "WavuWikiDiscordFeature"
        private const val BLUE = 0x00095FB
    }
}