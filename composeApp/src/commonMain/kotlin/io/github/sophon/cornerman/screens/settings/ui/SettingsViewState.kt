package io.github.sophon.cornerman.screens.settings.ui

import io.github.sophon.core.domain.FeatureInfo

internal data class SettingsViewState(
    val featureList: List<FeatureSetting> = listOf(),

    val isLoading: Boolean = false,
    val isError: String? = null,
) {
    data class FeatureSetting(
        val featureInfo: FeatureInfo,
        val isEnabled: Boolean,
    )

    companion object {
        internal val PREVIEW = SettingsViewState(
            featureList = listOf(
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "Wavu Wiki",
                        url = "",
                    ),
                    isEnabled = false,
                ),
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "Glossary Infil",
                        url = "",
                    ),
                    isEnabled = false,
                ),
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "SuperCombo",
                        url = "",
                    ),
                    isEnabled = false,
                ),
            )
        )
    }
}
