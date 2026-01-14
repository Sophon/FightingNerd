package io.github.sophon.discord.featureRegistry.wikiDustLoop

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.feature.WikiClientFeature
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.BotError
import io.github.sophon.discord.data.InMemoryCharacterListDB
import io.github.sophon.discord.data.InMemoryMoveListDB
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.Scheduler
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.usecase.CreateCharacterAliasesEmbedUseCase
import io.github.sophon.discord.usecase.GetCharacterUseCase
import io.github.sophon.discord.usecase.GetMoveUseCase
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import io.github.sophon.domain.Source
import io.github.sophon.wikidustloop.domain.DustLoopFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class DustLoopWikiDiscordFeature(
    dustLoopFeatureInfo: DustLoopFeatureInfo,
    private val syncWikiDataUseCase: SyncWikiDataUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val createCharacterAliasesEmbedUseCase: CreateCharacterAliasesEmbedUseCase,
    private val createMoveEmbedUseCase: CreateMoveEmbedUseCase,
    private val createCharacterEmbedUseCase: CreateCharacterEmbedUseCase,
    private val createDustLoopInvincibleMovesEmbedUseCase: CreateDustLoopInvincibleMovesEmbedUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    override val featureInfo: FeatureInfo = dustLoopFeatureInfo.featureInfo
    override val defaultCommand = SupportedCommand(
        command = Command.FD,
        description = "Global frame data",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_CHAR_NAME,
                description = "Character name",
            ),
            SupportedCommand.Argument(
                name = KEY_MOVE,
                description = "Move input"
            )
        )
    )
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.CHARGG,
            description = "GG character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            )
        ),
        SupportedCommand(
            command = Command.FDGG,
            description = "GG frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            )
        ),
        SupportedCommand(
            command = Command.INVGG,
            description = "GG invincible moves",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            )
        ),
        SupportedCommand(
            command = Command.CHARDB,
            description = "DB character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            ),
        ),
        SupportedCommand(
            command = Command.FDDB,
            description = "DB frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            ),
        ),
        SupportedCommand(
            command = Command.ALIASDB,
            description = "DB character aliases",
        ),
        SupportedCommand(
            command = Command.CHARBB,
            description = "BB character data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                )
            ),
        ),
        SupportedCommand(
            command = Command.FDBB,
            description = "BB frame data",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_CHAR_NAME,
                    description = "Character name",
                ),
                SupportedCommand.Argument(
                    name = KEY_MOVE,
                    description = "Move input"
                )
            ),
        ),
        SupportedCommand(
            command = Command.ALIASBB,
            description = "BB character aliases",
        ),
        SupportedCommand(
            command = Command.INVBB,
            description = "BB invincible moves",
        ),
    )
    private val wikis = mutableMapOf<String, WikiClient>()

    override fun registerGames(enabledGames: List<Game>) {
        val supportedGames = enabledGames.filter {
            it in featureInfo.supportedGameSet
        }
        supportedGames.forEach { game ->
            wikis[game.id] = get(named(WikiClientFeature.DustLoop.id)) {
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
        return when (command) {
            Command.FD -> {
                var lastError: BotError? = null
                for ((gameId, wiki) in wikis) {
                    val game = Game.fromId(gameId)
                    if (game == null) {
                        Result.Error(lastError ?: BotError.UnknownMove(query))
                    } else {
                        when (val result = searchMove(wiki, query, game)) {
                            is Result.Success -> return result
                            is Result.Error -> lastError = result.error
                        }
                    }
                }
                Result.Error(lastError ?: BotError.UnknownMove(query))
            }

            Command.CHARGG -> {
                val game = Game.GGST
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query, game)
            }
            Command.FDGG -> {
                val game = Game.GGST
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }
            Command.INVGG -> {
                val game = Game.GGST
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchInvincible(game, wiki, query)
            }

            Command.CHARDB -> {
                val game = Game.DBFZ
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query, game)
            }
            Command.FDDB -> {
                val game = Game.DBFZ
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }
            Command.ALIASDB -> {
                val wiki = wikis[Game.DBFZ.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }

            Command.CHARGB -> {
                val game = Game.GBVSR
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query, game)
            }
            Command.FDGB -> {
                val game = Game.GBVSR
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }

            Command.CHARBB -> {
                val game = Game.BBCF
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchCharacter(wiki, query, game)
            }
            Command.FDBB -> {
                val game = Game.BBCF
                val wiki = wikis[game.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchMove(wiki, query, game)
            }
            Command.ALIASBB -> {
                val wiki = wikis[Game.BBCF.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                getCharacterAliases(wiki)
            }
            Command.INVBB -> {
                val game = Game.BBCF
                val wiki = wikis[Game.BBCF.id]
                    ?: return Result.Error(BotError.UnsupportedGame(query))
                searchInvincible(game, wiki)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncData(): EmptyResult<BotError> {
        return syncWikiDataUseCase.invoke(wikiList = wikis.values)
    }

    private suspend fun searchCharacter(
        wiki: WikiClient,
        query: String,
        game: Game,
    ): Result<BotOutput, BotError> {
        return getCharacterUseCase.invoke(wiki = wiki, charName = query)
            .map { (character, fastestMoveList) ->
                BotOutput(
                    primaryEmbedBuilder = createCharacterEmbedUseCase.invoke(
                        character,
                        fastestMoveList,
                        game,
                        featureInfo,
                    )
                )
            }
    }

    private suspend fun searchMove(
        wiki: WikiClient,
        query: String,
        game: Game,
    ): Result<BotOutput, BotError> {
        return getMoveUseCase.invoke(wiki, query)
            .map { move ->
                val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
                    ?: emptyList()

                BotOutput(
                    primaryEmbedBuilder = createMoveEmbedUseCase.invoke(move, game, featureInfo),
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
        return createCharacterAliasesEmbedUseCase.invoke(wiki, featureInfo, RED)
            .map { embedBuilder ->
                BotOutput(primaryEmbedBuilder = embedBuilder)
            }
    }

    private suspend fun searchInvincible(
        game: Game,
        wiki: WikiClient,
        charName: String? = null,
    ): Result<BotOutput, BotError> {
        return createDustLoopInvincibleMovesEmbedUseCase.invoke(game, wiki, featureInfo, charName)
            .map { embedBuilder ->
                BotOutput(primaryEmbedBuilder = embedBuilder)
            }
    }


    private companion object {
        const val TAG = "DustLoopWikiDiscordFeature"
        const val KEY_CHAR_NAME = "character"
        const val KEY_MOVE = "move"
        const val RED = 0x00950117
    }
}