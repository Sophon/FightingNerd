package io.github.sophon.fightingnerd.feat.more.ui.featureSettings

import io.github.sophon.fightingnerd.BuildKonfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class FeatureSettingsState(
    val currentFeatureList: ImmutableList<UiFeatureSetting> = persistentListOf(),
    val updatedFeatureList: ImmutableList<UiFeatureSetting> = persistentListOf(),

    val appVersion: String = BuildKonfig.VERSION,

    val isLoading: Boolean = false,
    val isError: String? = null,
) {
    val isChanged: Boolean get() = (currentFeatureList != updatedFeatureList)

    data class UiFeatureSetting(
        val featureName: String,
        val iconUrl: String,
        val version: String,
        val gameList: List<UiGame>,
    ) {
        val isEnabled: Boolean get() = gameList.any { it.isEnabled }

        data class UiGame(
            val displayName: String,
            val id: String,
            val isEnabled: Boolean,
        )
    }

    companion object {
        internal val PREVIEW = FeatureSettingsState(
            currentFeatureList = persistentListOf(
                UiFeatureSetting(
                    featureName = "Wavu Wiki",
                    iconUrl = "",
                    version = "1.0.0",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Tekken 8", id = "T8", isEnabled = true),
                    ),
                ),
                UiFeatureSetting(
                    featureName = "SuperCombo",
                    iconUrl = "",
                    version = "1.0.9",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Street Fighter 6", id = "SF6", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "Mortal Kombat 1", id = "MK1", isEnabled = false),
                    ),
                ),
                UiFeatureSetting(
                    featureName = "Dustloop",
                    iconUrl = "",
                    version = "2.1.0",
                    gameList = listOf(
                        UiFeatureSetting.UiGame(displayName = "Guilty Gear Strive", id = "GGST", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "Granblue Fantasy Versus Rising", id = "GBVSR", isEnabled = true),
                        UiFeatureSetting.UiGame(displayName = "BlazBlue Central Fiction", id = "BBCF",  isEnabled = false),
                    ),
                ),
            ),
        )
    }
}