package io.github.sophon.fightingnerd.feat.home.ui

import io.github.sophon.core.wiki.domain.WikiClient

internal data class HomeViewState(
    val modules: List<WikiClient> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
