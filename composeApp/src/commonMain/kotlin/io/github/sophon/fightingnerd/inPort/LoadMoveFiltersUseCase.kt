package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.MoveFilter

interface LoadMoveFiltersUseCase {
    operator fun invoke(gameId: String): Result<Set<MoveFilter.Named>, AppError>
}
