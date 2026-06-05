package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character

interface CharacterListDB {
    suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun fetchCharacterList(): Result<List<Character>, WikiError>
    suspend fun wipe(): EmptyResult<WikiError>
    suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError>
}
