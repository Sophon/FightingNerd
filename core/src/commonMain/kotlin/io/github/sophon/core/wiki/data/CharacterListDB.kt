package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import kotlinx.datetime.Instant

interface CharacterListDB {
    suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError>
    suspend fun fetchCharacterList(): Result<List<Character>, WikiError>
    suspend fun wipe(): EmptyResult<WikiError>
    suspend fun fetchCharacterDataFor(charName: String): Result<Character, WikiError>
}
