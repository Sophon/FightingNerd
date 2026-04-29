package io.github.sophon.discord.feat.wikiWavu

import dev.kord.common.Color
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.data.InMemoryCharacterListDB
import io.github.sophon.discord.feat.core.data.InMemoryMoveListDB
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.Emoji
import io.github.sophon.discord.feat.core.ui.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.SearchStringFollowupsUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.domain.Source
import io.github.sophon.wikiwavu.domain.WavuFeatureInfo
import io.github.sophon.wikiwavu.domain.WavuFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.time.Duration.Companion.seconds

internal class WavuWikiDiscordFeature(
    wavuFeatureInfo: WavuFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getMovesUseCase: GetMovesUseCase,
    private val getStancesUseCase: GetStancesUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val searchStringFollowupsUseCase: SearchStringFollowupsUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
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
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }

        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.Wavu.id)) {
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
        val wiki = wikis[Game.Tekken8.id]
            ?: return Result.Error(BotError.UnsupportedGame(query))

        return when (command) {
            Command.Fd,
            Command.FdTK -> searchMove(wiki, query)

            Command.Pc -> searchPowerCrushMoves(wiki, query)
            Command.Heat -> searchHeatMoves(wiki, query)
            Command.Homing -> searchHomingMoves(wiki, query)
            Command.Stance -> getStancesUseCase.invoke(featureInfo, wiki, query)
            Command.AliasTK -> getCharacterAliases(wiki)
            Command.ThrowTK -> searchThrowMoves(wiki, query)
            Command.Strings -> searchStringFollowupsUseCase.invoke(wiki, query, featureInfo)
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