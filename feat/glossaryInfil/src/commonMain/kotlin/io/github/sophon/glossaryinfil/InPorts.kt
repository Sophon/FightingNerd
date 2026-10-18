package io.github.sophon.glossaryinfil

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem

interface RefreshGlossaryUseCase {
    suspend operator fun invoke(): EmptyResult<GlossaryError>
}

interface SearchGlossaryUseCase {
    suspend operator fun invoke(query: String): Result<List<GlossaryItem>, GlossaryError>
}
