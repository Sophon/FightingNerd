package io.github.sophon.fightingnerd.screens.home

import io.github.sophon.fightingnerd.feat.moduleList.model.WikiModule

internal data class HomeViewState(
    val wikiModules: List<WikiModule> = listOf(),
    val expandedFeatureIndex: Int? = null,

    val isLoading: Boolean = false,
    val error: String? = null,
)
