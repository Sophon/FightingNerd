package io.github.sophon.wikiwavu.integration

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.wikiwavu.BuildKonfig
import io.github.sophon.wikiwavu.domain.FEATURE_FEEDBACK_DISCORD_ID
import io.github.sophon.wikiwavu.domain.FEATURE_IMG_URL
import io.github.sophon.wikiwavu.domain.FEATURE_NAME
import io.github.sophon.wikiwavu.domain.FEATURE_URL

object WavuFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.Tekken8,
        ),
        version = BuildKonfig.VERSION,
        feedbackDiscordChannelId = FEATURE_FEEDBACK_DISCORD_ID,
    )
}