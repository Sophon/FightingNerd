package io.github.sophon.fightingnerd.core.usecase

import io.github.sophon.fightingnerd.core.data.ReviewPolicyRepo
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

internal class RecordInstallationUseCase(
    private val repo: ReviewPolicyRepo,
) {
    suspend operator fun invoke() {
        if (repo.getInstallationTimestamp().first() != null) return

        val now = Clock.System.now()
        repo.saveInstallationTimestamp(now)
    }
}
