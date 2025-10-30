package com.example.cornerman.screens.moveList.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoveListDao {
    @Query("SELECT * FROM moves WHERE input LIKE '%' || :query || '%'")
    suspend fun fetchMovesByInput(query: String): List<MoveEntity>

    @Query("SELECT * FROM moves WHERE charName = :charName")
    suspend fun fetchMoveListFor(charName: String): List<MoveEntity>

    @Query("SELECT * FROM moves WHERE charName = :charName AND input = :moveQuery")
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): MoveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoveList(moveList: List<MoveEntity>)

    @Query("DELETE FROM moves")
    suspend fun deleteAllMoves()
}