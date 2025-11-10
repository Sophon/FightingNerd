package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError

internal class ClearCacheUseCase(
    private val charDB: CharacterListDB,
    private val moveDB: MoveListDB,
) {
    suspend fun invoke(): EmptyResult<WikiError> {
        val charResult = charDB.wipe()
        val moveResult = moveDB.wipe()

        return when {
            charResult is Result.Error -> charResult
            moveResult is Result.Error -> moveResult
            else -> Result.Success(Unit)
        }
    }
}