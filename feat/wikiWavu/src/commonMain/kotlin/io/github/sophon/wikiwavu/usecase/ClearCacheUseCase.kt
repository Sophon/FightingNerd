package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError

class ClearCacheUseCase(
    private val charListDB: CharacterListDB,
    private val moveListDB: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WikiError> {
        val charResult = charListDB.wipe()
        val moveResult = moveListDB.wipe()

        return when {
            charResult is Result.Error -> charResult
            moveResult is Result.Error -> moveResult
            else -> Result.Success(Unit)
        }
    }
}