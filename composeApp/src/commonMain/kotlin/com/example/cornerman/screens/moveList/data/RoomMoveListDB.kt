package com.example.cornerman.screens.moveList.data

import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move

class RoomMoveListDB(
    private val dao: MoveDao,
): MoveListDB {
    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<Map<String, Move>, WavuError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String,
    ): Result<Move, WavuError> {
        TODO("Not yet implemented")
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>
    ) {
        TODO("Not yet implemented")
    }
}