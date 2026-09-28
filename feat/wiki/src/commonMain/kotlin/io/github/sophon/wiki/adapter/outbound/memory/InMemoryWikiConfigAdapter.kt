package io.github.sophon.wiki.adapter.outbound.memory

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import io.github.sophon.wiki.application.port.outbound.SaveWikiConfigPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class InMemoryWikiConfigAdapter : LoadWikiConfigPort, SaveWikiConfigPort {
    private val wikiConfig = MutableStateFlow<WikiConfig?>(null)

    override fun subscribe(): Flow<WikiConfig?> {
        return wikiConfig.asStateFlow()
    }

    override fun save(wikiConfig: WikiConfig): EmptyResult<WikiError> {
        this.wikiConfig.value = wikiConfig
        return Result.Success(Unit)
    }
}
