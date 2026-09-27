package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.WikiConfig
import kotlinx.coroutines.flow.Flow

interface LoadWikiConfigUseCase {
    operator fun invoke(): Flow<WikiConfig?>
}
