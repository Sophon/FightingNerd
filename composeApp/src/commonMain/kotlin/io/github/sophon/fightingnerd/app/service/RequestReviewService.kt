 package io.github.sophon.fightingnerd.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.app.model.SessionContext
import io.github.sophon.fightingnerd.app.outPort.InstallationPort
import io.github.sophon.fightingnerd.app.outPort.ReviewPort
import io.github.sophon.fightingnerd.inPort.RequestReviewUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

internal class RequestReviewService(
    private val installationPort: InstallationPort,
    private val reviewPort: ReviewPort,
    private val appScope: CoroutineScope,
): RequestReviewUseCase {
    override fun invoke(sessionContext: SessionContext) {
        appScope.launch {
            if (shouldTrigger(sessionContext).not()) return@launch

            Napier.d(tag = TAG) { "Review: triggering (${sessionContext::class.simpleName})" }
            reviewPort.requestReview()
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

        val isInstallationOldEnough = when (val timestampResult = installationPort.getInstallationTimestamp()) {
            is Result.Success -> {
                val isOldEnough = timestampResult.data?.let { instant ->
                    val age = (Clock.System.now() - instant)
                    age >= DURATION_INSTALLATION
                } ?: false
                isOldEnough
            }
            is Result.Error -> false
        }

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
}


private const val TAG = "RequestReviewService"
private val DURATION_SESSION = 10.seconds
private val DURATION_INSTALLATION = 7.days
private const val PCT_CORRECT_ANSWERS = 80
