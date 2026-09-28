package io.github.sophon.wiki.application.domain.model

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game

interface WikiClient {
    val featureInfo: FeatureInfo

    val supportedGameSet: Set<Game> get() {
        return featureInfo.supportedGameSet
    }
}
