package io.github.sophon.discord.feat.wikiSuperCombo

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
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
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import io.github.sophon.discord.feat.core.usecase.FetchMoveInWikisUseCase
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.discord.feat.core.usecase.GetMoveUseCase
import io.github.sophon.discord.feat.core.usecase.SyncWikiDataUseCase
import io.github.sophon.discord.util.withWiki
import io.github.sophon.integration.model.Source
import io.github.sophon.wikiSuperCombo.integration.SuperComboFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class SuperComboWikiDiscordFeature(
    superComboFeatureInfo: SuperComboFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val fetchMoveInWikisUseCase: FetchMoveInWikisUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, GameWikiDiscordFeature, KoinComponent {
    override val featureInfo = superComboFeatureInfo.featureInfo
    override val defaultCommand = Command.Fd
    override val otherCommands = listOf(
        Command.FdSF,
        Command.CharSF,
        Command.FdMK,
        Command.CharMK,
        Command.FdAV,
        Command.CharAV,
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

            Command.CharSF -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.StreetFighter6,
                    query = query,
                ) { _, wiki, query -> searchCharacter(wiki, query) }
            }
            Command.FdSF -> withWiki(
                wikis = wikiClientMap,
                game = Game.StreetFighter6,
                query = query,
            ) { _, wiki, query -> searchMove(wiki, query) }

            Command.CharMK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.MK1,
                    query = query,
                ) { _, wiki, query -> searchCharacter(wiki, query) }
            }
            Command.FdMK -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.MK1,
                    query = query,
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            Command.CharAV -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.AVL,
                    query = query,
                ) { _, wiki, query -> searchCharacter(wiki, query) }
            }
            Command.FdAV -> {
                withWiki(
                    wikis = wikiClientMap,
                    game = Game.AVL,
                    query = query,
                ) { _, wiki, query -> searchMove(wiki, query) }
            }

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }

    override suspend fun refreshData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikiClientMap.values)
    }

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki, charName = query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder =
                        superComboCharacterEmbed(
                            character,
                            fastestMoveList,
                            featureInfo,
                        )
                )
            }
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    mutableEmbedBuilder = BotOutput.MutableEmbedBuilder(
                        primaryBuilder = superComboMoveEmbed(move, featureInfo),
                        manualEditBuilder = superComboMoveDetailedEmbed(move, featureInfo),
                    ),
                    buttons = BotOutput.ButtonSet(
                        buttonList = listOf(
                            BotOutput.EmbedButton(
                                label = "Details", action = BotOutput.EmbedButton.Action.Edit()
                            ),
                        )
                    ),
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


    private companion object {
        const val TAG = "SuperComboWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
    }
}