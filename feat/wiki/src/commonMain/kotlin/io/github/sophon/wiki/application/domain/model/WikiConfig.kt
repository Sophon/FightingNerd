package io.github.sophon.wiki.application.domain.model

import io.github.sophon.core.featureConfig.model.Game

data class WikiConfig(
    val availableGameSet: Set<Game>,
    val enabledGameSet: Set<Game>,
) {
    init {
        require(availableGameSet.containsAll(enabledGameSet)) {
            "Enabled games must be available: ${enabledGameSet - availableGameSet}"
        }
    }
}
