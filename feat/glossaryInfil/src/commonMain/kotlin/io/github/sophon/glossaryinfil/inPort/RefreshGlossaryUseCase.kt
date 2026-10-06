package io.github.sophon.glossaryinfil.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.glossaryinfil.model.GlossaryError

interface RefreshGlossaryUseCase {
    suspend operator fun invoke(): EmptyResult<GlossaryError>
}
