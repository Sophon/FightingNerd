package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Move
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveDto
import io.github.sophon.wikiwavu.data.MoveListResponseDto
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.mapToDomain

internal class DownloadMoveListUseCase(
    private val source: WavuWikiDataSource,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WavuError> {
        return when (val result = source.fetchMoveList(charName)) {
            is Result.Success -> {
                val downloadedMoves: List<MoveDto> = result.data.extractDto()
                val movesById = downloadedMoves.associateBy { it.id }

                val moveList: List<Move> = downloadedMoves
                    .map { it.mapToDomain(charName, movesById) }

                Result.Success(moveList)
            }
            is Result.Error -> {
                Result.Error(WavuError.DOWNLOAD_ERROR)
            }
        }
    }


    private fun MoveListResponseDto.extractDto(): List<MoveDto> = cargoQuery.map { it.title }
}