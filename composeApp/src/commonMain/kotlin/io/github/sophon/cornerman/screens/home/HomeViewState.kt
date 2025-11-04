package io.github.sophon.cornerman.screens.home

import io.github.sophon.cornerman.featureRegistry.ComposeRegisteredFeature

data class HomeViewState(
    val composeRegisteredFeatures: List<ComposeRegisteredFeature> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
