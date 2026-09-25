package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import kotlinx.coroutines.flow.Flow

internal class RefreshDataService : RefreshDataUseCase {
    override fun invoke(): Flow<RefreshEvent> {
        TODO("Not yet implemented")
    }
}
