package io.github.sophon.discord.feat.dreamCancel

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
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.dreamcancel.integration.DreamCancelFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent

internal class DreamCancelWikiDiscordFeature(
    dreamCancelFeatureInfo: DreamCancelFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dreamCancelFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.FdKOF,
        Command.AliasKOF,
        Command.FdCOTW,
        Command.AliasCOTW,
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
            Command.Fd -> fetchMoveInWikisUseCase.invoke(
                wikis = wikiClientMap,
                query = query,
                searchFun = ::searchMove,
            )

            Command.FdKOF -> withWiki(
                wikis = wikiClientMap,
                game = Game.KoFXV,
                query = query,
                action = ::searchMove,
            )
            Command.AliasKOF -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.KoFXV,
                    query = query,
                ) { _, wiki, _ ->
                    getCharacterAliases(wiki)
                }
            }

            Command.FdCOTW -> withWiki(
                wikis = wikiClientMap,
                game = Game.COTW,
                query = query,
                action = ::searchMove,
            )
            Command.AliasCOTW -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.COTW,
                    query = query,
                ) { _, wiki, _ ->
                    getCharacterAliases(wiki)
                }
            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }

    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }


    private suspend fun searchMove(
        game: Game,
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    primaryEmbedBuilder = dreamCancelMoveEmbed(game, move, featureInfo),
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

    private suspend fun getCharacterAliases(wiki: WikiClient): Result<BotOutput, BotError> {
        return createCharacterAliasesEmbedUseCase.invoke(
            wiki = wiki,
            featureInfo = featureInfo,
            colorCode = BLUE,
        ).map { BotOutput(primaryEmbedBuilder = it) }
    }


    private companion object {
        const val TAG = "DreamCancelWikiDiscordFeature"
        const val BLUE = 0x009AB3F6
    }
}