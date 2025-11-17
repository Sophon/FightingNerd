package io.github.sophon.discord.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

class InMemoryCharacterListDB: CharacterListDB {
    private var database: MutableMap<String, Character> = mutableMapOf()

    override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        characterList.forEach { character ->
            database.put(key = character.id, value = character)
            character.aliasList.forEach { alias ->
                database.put(key = alias, value = character)
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        return Result.Success(database.values.toList())
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        database.clear()
        return Result.Success(Unit)
    }

    override suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError> {
        val character: Character = database[charName]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))
        return Result.Success(character)
    }
}