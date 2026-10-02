package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.port.outbound.GetMovesOfTypePort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterListUseCase
import io.github.sophon.wiki.application.port.inbound.GetCharacterUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import io.github.sophon.wiki.application.port.inbound.GetMoveUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val getMoveListUseCase: GetMoveListUseCase,
    private val getCharacterUseCase: GetCharacterUseCase,
    private val getMoveUseCase: GetMoveUseCase,
): ConfigureWikiPort, RefreshWikiPort, FrameDataPort, GetMovesOfTypePort {
    override suspend fun configure(discordConfig: DiscordConfig): EmptyResult<BotError> {
        val result = discordConfig.toWikiConfig()
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refresh() {
        refreshDataUseCase().collect()
    }

    override suspend fun getFrameData(query: String): Result<BotResponse, BotError> {
        val characterQuery = query.split(" ").first()
        val character = findCharacter(characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(characterQuery))

        val moveQuery = query.substringAfter(delimiter = " ", missingDelimiterValue = "")
        val move = findMove(character.id, moveQuery)
            ?: return Result.Error(BotError.UnknownMove(characterQuery, moveQuery))

        val botResponse = move.toDomain(character)
        return Result.Success(botResponse)
    }

    override suspend fun getFrameData(moveId: MoveId): Result<BotResponse.MoveResponse, BotError> {
        val characterId = CharacterId(game = moveId.game, naturalId = moveId.characterId)
        val result = getCharacterUseCase(characterId)
            .flatMap { character ->
                val moveResponse = getMoveUseCase(characterId, moveId.input).map { it.toDomain(character) }
                moveResponse
            }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun getMovesOfType(
        characterQuery: String,
        moveType: MoveType,
    ): Result<BotResponse.ListResponse, BotError> {
        val character = findCharacter(characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(characterQuery))

        val moveList = getMoveListUseCase(characterId = character.id)
            .first()
            .filter(moveType.toFilter().predicate)

        val listResponse = moveList.toListResponse(character, moveType)
        return Result.Success(listResponse)
    }


    private suspend fun findCharacter(characterQuery: String): Character? {
        val characterList = getCharacterListUseCase().first()
        val character = characterList.firstOrNull { it.matches(characterQuery) }
        return character
    }

    private suspend fun findMove(characterId: CharacterId, moveQuery: String): Move? {
        val moveList = getMoveListUseCase(characterId = characterId).first()
        val move = moveList.firstOrNull { it.matches(moveQuery) }
        return move
    }

    private fun Character.matches(characterQuery: String): Boolean {
        return id.naturalId.equalsIgnoreCase(characterQuery)
                || aliasList.any { it.equalsIgnoreCase(characterQuery) }
    }

    private fun Move.matches(moveQuery: String): Boolean {
        return input.equalsIgnoreCase(moveQuery)
                || name.equalsIgnoreCase(moveQuery)
                || aliases.any { it.equalsIgnoreCase(moveQuery) }
    }
}
