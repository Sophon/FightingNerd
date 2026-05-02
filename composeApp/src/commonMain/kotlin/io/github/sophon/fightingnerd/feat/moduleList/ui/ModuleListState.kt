package io.github.sophon.fightingnerd.feat.moduleList.ui

import io.github.sophon.fightingnerd.feat.moduleList.model.WikiModule

internal data class ModuleListState(
    val moduleList: List<WikiModule> = listOf(),
    val expandedModuleIndex: Int? = null,

    val error: String? = null,
)