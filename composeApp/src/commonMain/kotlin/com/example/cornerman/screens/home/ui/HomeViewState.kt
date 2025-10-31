package com.example.cornerman.screens.home.ui

import com.example.cornerman.featureRegistry.RegisteredFeature

data class HomeViewState(
    val registeredFeatures: List<RegisteredFeature> = listOf(),

    val isLoading: Boolean = false,
    val error: String? = null,
)
