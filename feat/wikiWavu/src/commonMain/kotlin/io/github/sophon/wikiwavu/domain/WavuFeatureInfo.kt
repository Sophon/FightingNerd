package io.github.sophon.wikiwavu.domain

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.wikiwavu.BuildKonfig
import io.github.sophon.wikiwavu.FEATURE_IMG_URL
import io.github.sophon.wikiwavu.FEATURE_NAME
import io.github.sophon.wikiwavu.FEATURE_URL
import io.github.sophon.wikiwavu.data.WavuTables

object WavuFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.Tekken8,
        ),
        version = BuildKonfig.VERSION,
    )
}