package io.github.sophon.cornerman.screens.home

import io.github.sophon.cornerman.featureRegistry.RegisteredFeature

data class HomeViewState(
    val registeredFeatures: List<RegisteredFeature> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
