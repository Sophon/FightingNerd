package io.github.sophon.fightingnerd.feat.settings.ui

import io.github.sophon.fightingnerd.BuildKonfig

internal data class SettingsState(
    val featureList: List<UiFeatureSetting> = listOf(),

    val appVersion: String = BuildKonfig.VERSION,

    val isLoading: Boolean = false,
    val isError: String? = null,
) {
    data class UiFeatureSetting(
        val featureName: String,
        val iconUrl: String,
        val version: String,
        val gameList: List<UiGame>,
    ) {
        val isEnabled: Boolean get() = gameList.any { it.isEnabled }

        data class UiGame(
            val displayName: String,
            val isEnabled: Boolean,
        )
    }

    companion object {
        internal val PREVIEW = SettingsState(
            featureList = listOf(
                UiFeatureSetting(
                    featureName = "Wavu Wiki",
                    iconUrl = "",
                    version = "1.0.0",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Tekken 8", isEnabled = true),
                    ),
                ),
                UiFeatureSetting(
                    featureName = "SuperCombo",
                    iconUrl = "",
                    version = "1.0.9",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Street Fighter 6", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "Mortal Kombat 1", isEnabled = false),
                    ),
                ),
                UiFeatureSetting(
                    featureName = "Dustloop",
                    iconUrl = "",
                    version = "2.1.0",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Guilty Gear Strive", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "Granblue Fantasy Versus Rising", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "BlazBlue Central Fiction", isEnabled = false),
                    ),
                ),
            ),
        )
    }
}
