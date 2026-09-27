package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.WikiConfig
import kotlinx.coroutines.flow.Flow

internal interface LoadWikiConfigPort {
    fun subscribe(): Flow<WikiConfig?>
}
