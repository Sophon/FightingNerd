package io.github.sophon.discord.feat.wikiWavu

import dev.kord.common.Color
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.featureConfig.model.Game
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
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.SearchStringFollowupsUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.WavuFilter
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
        return when (command) {
            Command.Fd -> {
                fetchMoveInWikisUseCase.invoke(
                    wikis = wikiClientMap,
                    query = query,
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            Command.FdTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            Command.Pc -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchPowerCrushMoves(wiki, query) }
            }
            Command.Heat -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchHeatMoves(wiki, query) }
            }
            Command.Homing -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchHomingMoves(wiki, query) }
            }
            Command.ThrowTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchThrowMoves(wiki, query) }
            }

            Command.AliasTK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, _ -> getCharacterAliases(wiki) }
            }

            Command.Stance -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> getStancesUseCase.invoke(featureInfo, wiki, query) }
            }
            Command.Strings -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.Tekken8,
                    query = query,
                ) { _, wiki, query -> searchStringFollowupsUseCase.invoke(wiki, query, featureInfo) }
            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                BotOutput(primaryEmbedBuilder = wavuMoveEmbed(move, featureInfo))
            }
    }

    private suspend fun searchPowerCrushMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = query,
            filter = WavuFilter.PowerCrush,
        )
            .map { moveList ->
                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = "${query.uppercase()} Power Crush",
                        dataList = moveList.map { it.input },
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                        emoji = Emoji.TK_PC,
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = query),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
            }
    }

    private suspend fun searchHeatMoves(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = query,
            filter = WavuFilter.Heat,
        ).map { moveList ->
                BotOutput(
                    primaryEmbedBuilder = moveListEmbed(
                        category = "${query.uppercase()} Heat",
                        dataList = moveList.map { it.input },
                        featureInfo = featureInfo,
                        color = Color(BLUE),
                        emoji = Emoji.TK_HEAT,
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = moveList.toButtons(charName = query),
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
            charName = query,
            filter = WavuFilter.Homing,
        ).map { moveList ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${query.uppercase()} Homing",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                    emoji = Emoji.TK_HOMING,
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = query),
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
            charName = query,
            filter = WavuFilter.Throw,
        ).map { moveList ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${query.uppercase()} Throw",
                    dataList = moveList.map { it.input },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                    emoji = Emoji.THROW,
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = query),
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