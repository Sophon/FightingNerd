package io.github.sophon.glossaryinfil.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem

interface SearchGlossaryUseCase {
    suspend operator fun invoke(query: String): Result<List<GlossaryItem>, GlossaryError>
}
