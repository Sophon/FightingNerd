package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.inPort.RefreshGamesUseCase
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList

internal class RefreshGamesService(
    private val refreshWikiPort: RefreshWikiPort,
): RefreshGamesUseCase {
    override suspend fun invoke(gameIdSet: Set<String>): EmptyResult<AppError> {
        val failedEventList = refreshWikiPort.refresh(gameIdSet)
            .filterIsInstance<RefreshEvent.Failure>()
            .toList()
        val failedEvent = failedEventList.firstOrNull()
        val result = if (failedEvent == null) {
            Result.Success(Unit)
        } else {
            Result.Error(failedEvent.error)
        }
        return result
    }
}
