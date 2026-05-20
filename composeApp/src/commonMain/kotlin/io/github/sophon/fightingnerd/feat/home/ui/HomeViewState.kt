package io.github.sophon.fightingnerd.feat.home.ui

import io.github.sophon.fightingnerd.core.model.Module

internal data class HomeViewState(
    val modules: List<Module> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
