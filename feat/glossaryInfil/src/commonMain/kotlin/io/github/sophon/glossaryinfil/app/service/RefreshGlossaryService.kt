package io.github.sophon.glossaryinfil.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.glossaryinfil.app.outPort.FetchGlossaryPort
import io.github.sophon.glossaryinfil.app.outPort.ReplaceGlossaryPort
import io.github.sophon.glossaryinfil.inPort.RefreshGlossaryUseCase
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class RefreshGlossaryService(
    private val fetchGlossaryPort: FetchGlossaryPort,
    private val replaceGlossaryPort: ReplaceGlossaryPort,
): RefreshGlossaryUseCase {
    private val refreshMutex = Mutex()

    override suspend fun invoke(): EmptyResult<GlossaryError> {
        val result = refreshMutex.withLock {
            fetchGlossaryPort.fetch()
                .mapError { error -> GlossaryError.Download(error) }
                .flatMap { itemList -> replaceGlossary(itemList) }
                .onError { error -> Napier.e(tag = TAG) { "refresh failed: $error" } }
        }
        return result
    }

    private suspend fun replaceGlossary(itemList: List<GlossaryItem>): EmptyResult<GlossaryError> {
        // an empty download is a broken source, not an empty glossary - keep the stored one
        if (itemList.isEmpty()) return Result.Error(GlossaryError.EmptyGlossary)

        val result = replaceGlossaryPort.replace(itemList)
            .mapError { error -> GlossaryError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "${itemList.size} glossary items saved" } }
        return result
    }


    private companion object {
        const val TAG = "RefreshGlossaryService"
    }
}
