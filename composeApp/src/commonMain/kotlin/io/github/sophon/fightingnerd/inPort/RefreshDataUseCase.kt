package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

interface RefreshDataUseCase {
    operator fun invoke(): Flow<RefreshEvent>
}
