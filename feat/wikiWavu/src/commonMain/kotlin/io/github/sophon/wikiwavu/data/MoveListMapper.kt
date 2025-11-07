package io.github.sophon.wikiwavu.data

import io.github.sophon.core.wiki.domain.model.Move

internal fun toDomain(dto: MoveListResponseDto, charName: String): List<Move> {
    val downloadedMoves = dto.extractMoveDto()
    val movesById = downloadedMoves.associateBy { it.id }
    return downloadedMoves.map { it.mapToDomain(charName, movesById) }
}