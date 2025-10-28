package com.example.wikiwavu.usecase

import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveDto
import com.example.wikiwavu.data.MoveListResponseDto
import com.example.wikiwavu.data.WavuWikiDataSource
import com.example.wikiwavu.domain.mapToDomain
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.domain.model.CharacterMoveList
import com.example.wikiwavu.domain.model.Move

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(character: Character): Result<CharacterMoveList, WavuError> {
        return when (val result = source.fetchMoveList(character.name)) {
            is Result.Success -> {
                val downloadedMoves: List<MoveDto> = result.data.extractDto()
                val movesById = downloadedMoves.associateBy { it.id }

                val moveList: List<Move> = downloadedMoves
                    .map { it.mapToDomain(character, movesById) }

                Result.Success(CharacterMoveList(character, moveList))
            }
            is Result.Error -> {
                Result.Error(WavuError.DOWNLOAD_ERROR)
            }
        }
    }


    private fun MoveListResponseDto.extractDto(): List<MoveDto> = cargoQuery.map { it.title }
}