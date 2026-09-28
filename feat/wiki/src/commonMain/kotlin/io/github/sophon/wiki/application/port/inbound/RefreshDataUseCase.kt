package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.wiki.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

interface RefreshDataUseCase {
    operator fun invoke(): Flow<RefreshEvent>
}
