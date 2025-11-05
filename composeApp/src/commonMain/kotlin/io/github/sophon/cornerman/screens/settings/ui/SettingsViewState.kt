package io.github.sophon.cornerman.screens.settings.ui

import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature

internal data class SettingsViewState(
    val featureList: List<FeatureSetting> = listOf(),
) {
    data class FeatureSetting(
        val feature: ComposeRegisteredFeature,
        val isEnabled: Boolean,
    )
}
