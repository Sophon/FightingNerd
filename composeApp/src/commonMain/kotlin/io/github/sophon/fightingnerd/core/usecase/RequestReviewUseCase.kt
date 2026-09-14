package io.github.sophon.fightingnerd.core.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.data.ReviewPolicyRepo
import io.github.sophon.fightingnerd.feat.review.platform.ReviewHandler
import io.github.sophon.fightingnerd.feat.review.SessionContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

internal class RequestReviewUseCase(
    private val reviewPolicyRepo: ReviewPolicyRepo,
    private val reviewHandler: ReviewHandler,
    private val appScope: CoroutineScope,
) {
    operator fun invoke(sessionContext: SessionContext) {
        appScope.launch {
            if (shouldTrigger(sessionContext).not()) return@launch

            Napier.d(tag = TAG) { "Review: triggering (${sessionContext::class.simpleName})" }
            reviewHandler.requestReview()
                .onSuccess {
                    Napier.d(tag = TAG) { "Review: Success" }
                }
                .onError { error ->
                    Napier.d(tag = TAG) { error.errorMessage }
                }
        }
    }

    private suspend fun shouldTrigger(sessionContext: SessionContext): Boolean {
        val isSessionLongEnough = (sessionContext.duration >= DURATION_SESSION)

        val isInstallationOldEnough = reviewPolicyRepo.getInstallationTimestamp().first()?.let { instant ->
            val age = (Clock.System.now() - instant)
            age >= DURATION_INSTALLATION
        } ?: false

        val otherRequirementsMet = sessionContext.otherRequirementsMet()

        val shouldTrigger = isSessionLongEnough && isInstallationOldEnough && otherRequirementsMet
        if (shouldTrigger.not()) {
            Napier.d(tag = TAG) {
                "Review: skipped (${sessionContext::class.simpleName}) - " +
                        "session duration = ${isSessionLongEnough}, " +
                        "install age = ${isInstallationOldEnough}, " +
                        "other = $otherRequirementsMet"
            }
        }

        return shouldTrigger
    }

    private fun SessionContext.otherRequirementsMet(): Boolean {
        val isMet = when (this) {
            is SessionContext.Quiz -> {
                this.correctAnswerPct >= PCT_CORRECT_ANSWERS
            }
            else -> true
        }
        return isMet
    }


    private companion object {
        const val TAG = "RequestReviewUseCase"
        val DURATION_SESSION = 10.seconds
        val DURATION_INSTALLATION = 7.days
        const val PCT_CORRECT_ANSWERS = 80
    }
}
