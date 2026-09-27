package io.github.sophon.wiki.application.domain.service

import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.port.inbound.LoadWikiConfigUseCase
import io.github.sophon.wiki.application.port.outbound.LoadWikiConfigPort
import kotlinx.coroutines.flow.Flow

internal class LoadWikiConfigService(
    private val loadWikiConfigPort: LoadWikiConfigPort,
): LoadWikiConfigUseCase {
    override fun invoke(): Flow<WikiConfig?> {
        return loadWikiConfigPort.subscribe()
    }


    private companion object {
        const val TAG = "LoadWikiConfigService"
    }
}
