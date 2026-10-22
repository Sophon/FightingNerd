package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.MoveFilter
import io.github.sophon.fightingnerd.app.outPort.MoveFilterPort
import io.github.sophon.fightingnerd.inPort.LoadMoveFiltersUseCase

@ExcludeFromCoverage("plain port call")
internal class LoadMoveFiltersService(
    private val moveFilterPort: MoveFilterPort,
): LoadMoveFiltersUseCase {
    override fun invoke(gameId: String): Result<Set<MoveFilter.Named>, AppError> {
        val result = moveFilterPort.loadFilters(gameId)
        return result
    }
}
