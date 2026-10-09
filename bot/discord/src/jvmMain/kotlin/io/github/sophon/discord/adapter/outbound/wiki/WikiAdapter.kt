package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.FrameRange
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.frameData.MoveType
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.outPort.CharactersPort
import io.github.sophon.discord.app.outPort.ConfigureWikiPort
import io.github.sophon.discord.app.outPort.FrameDataPort
import io.github.sophon.discord.app.outPort.GamePort
import io.github.sophon.discord.app.outPort.GetMovesInRangePort
import io.github.sophon.discord.app.outPort.GetMovesOfTypePort
import io.github.sophon.discord.app.outPort.NormalizeMoveInputPort
import io.github.sophon.discord.app.outPort.RefreshWikiPort
import io.github.sophon.wiki.ConfigureWikiUseCase
import io.github.sophon.wiki.GetAvailableGamesUseCase
import io.github.sophon.wiki.GetCharacterListUseCase
import io.github.sophon.wiki.GetCharacterUseCase
import io.github.sophon.wiki.GetMoveListUseCase
import io.github.sophon.wiki.GetMoveUseCase
import io.github.sophon.wiki.NormalizeMoveInputUseCase
import io.github.sophon.wiki.RefreshDataUseCase
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import io.github.sophon.wiki.model.CharacterId as WikiCharacterId

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val getMoveListUseCase: GetMoveListUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
    private val getAvailableGamesUseCase: GetAvailableGamesUseCase,
    private val normalizeMoveInputUseCase: NormalizeMoveInputUseCase,
): ConfigureWikiPort, RefreshWikiPort, FrameDataPort, GetMovesOfTypePort, GetMovesInRangePort, CharactersPort, GamePort,
    NormalizeMoveInputPort {
    override suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
        val result = discordConfig.toWikiConfig()
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refresh() {
        refreshDataUseCase().collect()
    }

    override suspend fun getMoves(characterId: CharacterId): Result<List<MoveResponse>, BotError> {
        val result = getMoveResponses(characterId = characterId, filter = Filter.None)
        return result
    }

    override suspend fun getFrameData(moveId: MoveId): Result<MoveResponse, BotError> {
        val characterId = WikiCharacterId(game = moveId.game, naturalId = moveId.characterId)
        val result = getCharacterUseCase(characterId)
            .flatMap { character ->
                val moveResponse = getMoveUseCase(characterId, moveId.input).map { it.toDomain(character) }
                moveResponse
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun getMovesOfType(
        characterId: CharacterId,
        moveType: MoveType,
    ): Result<List<MoveResponse>, BotError> {
        val result = getMoveResponses(characterId = characterId, filter = moveType.toFilter())
        return result
    }

    override suspend fun getMovesInRange(
        characterId: CharacterId,
        frameRange: FrameRange,
    ): Result<List<MoveResponse>, BotError> {
        val result = getMoveResponses(characterId = characterId, filter = frameRange.toFilter())
        return result
    }

    override suspend fun getCharacters(): List<CharacterResponse> {
        val characterList = getCharacterListUseCase()
            .first()
            .map { it.toDomain() }
        return characterList
    }

    override suspend fun getGameList(): GameList {
        val gameList = getAvailableGamesUseCase()
            .first()
            .toGameList()
        return gameList
    }

    override suspend fun findGame(gameId: String): Result<Game, BotError> {
        val game = getAvailableGamesUseCase()
            .first()
            .firstOrNull { it.id == gameId }
        val result = if (game != null) {
            Result.Success(game)
        } else {
            Result.Error(BotError.UnsupportedGame(gameId))
        }
        return result
    }

    override fun normalizeMoveInput(game: Game, input: String): String {
        val normalized = normalizeMoveInputUseCase(game, input)
        return normalized
    }


    private suspend fun getMoveResponses(
        characterId: CharacterId,
        filter: Filter,
    ): Result<List<MoveResponse>, BotError> {
        val wikiCharacterId = characterId.toWikiCharacterId()
        val result = getCharacterUseCase(wikiCharacterId)
            .map { character ->
                val moveList = getMoveListUseCase(characterId = wikiCharacterId)
                    .first()
                    .filter(filter.predicate)
                    .map { it.toDomain(character) }
                moveList
            }
            .mapError { it.toDomainError() }
        return result
    }
}
