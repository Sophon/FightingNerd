package io.github.sophon.fightingnerd.screens.home

import io.github.sophon.fightingnerd.featureRegistry.ComposeRegisteredFeature

internal data class HomeViewState(
    val composeRegisteredFeatures: List<ComposeRegisteredFeature> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
