package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui

import io.github.sophon.core.wiki.domain.model.Character

data class SuperComboHomeScreenViewState(
    val characterList: List<Character> = listOf(),
    val isExpanded: Boolean = false,

    val isLoading: Boolean = true,
    val error: String? = null,
) {
    companion object {
        //TODO: preview
    }
}
