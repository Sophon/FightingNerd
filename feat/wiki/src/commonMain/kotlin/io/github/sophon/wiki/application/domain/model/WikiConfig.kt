package io.github.sophon.wiki.application.domain.model

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game

@ConsistentCopyVisibility
data class WikiConfig private constructor(
    val availableGameSet: Set<Game>,
    val enabledGameSet: Set<Game>,
) {
    companion object {
        /**
         * Returns [WikiError.InvalidConfig] with the IDs of enabled games that aren't available.
         */
        fun create(
            availableGameSet: Set<Game>,
            enabledGameSet: Set<Game>,
        ): Result<WikiConfig, WikiError> {
            val enabledButUnavailable = (enabledGameSet - availableGameSet)
            val result = if (enabledButUnavailable.isEmpty()) {
                Result.Success(WikiConfig(availableGameSet, enabledGameSet))
            } else {
                val erroneousGameIdList = enabledButUnavailable.map { it.id }
                Result.Error(WikiError.InvalidConfig(*erroneousGameIdList.toTypedArray()))
            }
            return result
        }
    }
}
