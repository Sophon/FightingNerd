package io.github.sophon.discord.feat.core.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character

internal class InMemoryCharacterListDB: CharacterListDB {
    private var database: MutableMap<String, Character> = mutableMapOf()
    private val charNameAliasMap: MutableMap<String, String> = mutableMapOf()

    override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        characterList.forEach { character ->
            database.put(key = character.id, value = character)
            character.aliasList.forEach { alias ->
                charNameAliasMap.put(key = alias, value = character.id)
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
        val characterId = if (database.containsKey(charName)) {
            charName
        } else {
            charNameAliasMap[charName]
        }

        val character: Character = database[characterId]
            ?: return Result.Error(WikiError.UnknownCharacter(charName))

        return Result.Success(character)
    }
}