package io.github.sophon.fightingnerd.screens.home.data

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

class RoomCharacterListDB(
    private val dao: CharacterListDao,
): CharacterListDB {
    override suspend fun insertCharacterList(
        characterList: List<Character>
    ): EmptyResult<WikiError> {
        return try {
            dao.insertCharacterList(characterList.map { it.toEntity() })
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        return try {
            val characterEntities = dao.fetchAllCharacters()
            Result.Success(characterEntities.map { it.toDomain() })
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        return try {
            dao.deleteAllCharacters()
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun fetchCharacterDataFor(
        charName: String
    ): Result<Character, WikiError> {
        return try {
            val charEntity = dao.fetchCharacterData(charName)
            if (charEntity == null) {
                Result.Error(WikiError.UNKNOWN_MOVE)
            } else {
                Result.Success(charEntity.toDomain())
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }


    private companion object {
        const val TAG = "RoomCharacterListDB"
    }
}