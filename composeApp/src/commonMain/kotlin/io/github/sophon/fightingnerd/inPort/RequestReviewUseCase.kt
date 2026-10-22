package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.SessionContext

interface RequestReviewUseCase {
    operator fun invoke(sessionContext: SessionContext)
}
