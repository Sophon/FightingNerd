package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.port.outbound.CharactersPort
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.port.outbound.GetMovesOfTypePort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort
import io.github.sophon.wiki.application.domain.model.Filter
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterListUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import io.github.sophon.wiki.application.domain.model.CharacterId as WikiCharacterId

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val getMoveListUseCase: GetMoveListUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
): ConfigureWikiPort, RefreshWikiPort, FrameDataPort, GetMovesOfTypePort, CharactersPort {
    override suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
        val result = discordConfig.toWikiConfig()
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refresh() {
        refreshDataUseCase().collect()
    }

    override suspend fun getMoves(characterId: CharacterId): Result<List<BotResponse.MoveResponse>, BotError> {
        val result = getMoveResponses(characterId = characterId, filter = Filter.None)
        return result
    }

    override suspend fun getFrameData(moveId: MoveId): Result<BotResponse.MoveResponse, BotError> {
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
    ): Result<List<BotResponse.MoveResponse>, BotError> {
        val result = getMoveResponses(characterId = characterId, filter = moveType.toFilter())
        return result
    }

    override suspend fun getCharacters(): List<BotResponse.CharacterResponse> {
        val characterList = getCharacterListUseCase()
            .first()
            .map { it.toDomain() }
        return characterList
    }


    private suspend fun getMoveResponses(
        characterId: CharacterId,
        filter: Filter,
    ): Result<List<BotResponse.MoveResponse>, BotError> {
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
