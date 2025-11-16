package io.github.sophon.fightingnerd.screens.settings.ui

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.BuildKonfig

internal data class SettingsViewState(
    val featureList: List<FeatureSetting> = listOf(),

    val appVersion: String = BuildKonfig.VERSION,

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
                        version = "1.0.0",
                    ),
                    isEnabled = false,
                ),
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "Glossary Infil",
                        url = "",
                        version = "1.5.0",
                    ),
                    isEnabled = false,
                ),
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "SuperCombo",
                        url = "",
                        version = "1.0.9",
                    ),
                    isEnabled = true,
                ),
                FeatureSetting(
                    featureInfo = FeatureInfo(
                        name = "Dustloop",
                        url = "",
                        version = "2.1.0",
                    ),
                    isEnabled = true,
                ),
            )
        )
    }
}
