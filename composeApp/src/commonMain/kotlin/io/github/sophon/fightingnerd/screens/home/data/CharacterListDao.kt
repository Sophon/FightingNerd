package io.github.sophon.fightingnerd.screens.home.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterList(characterList: List<CharacterEntity>)

    @Query("SELECT * FROM characters WHERE displayName = :charName")
    suspend fun fetchCharacterData(charName: String): CharacterEntity?

    @Query("SELECT * FROM characters")
    suspend fun fetchAllCharacters(): List<CharacterEntity>

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()
}