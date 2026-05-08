package io.github.sophon.fightingnerd.screens.home

import io.github.sophon.fightingnerd.feat.config.model.Module

internal data class HomeViewState(
    val modules: List<Module> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
