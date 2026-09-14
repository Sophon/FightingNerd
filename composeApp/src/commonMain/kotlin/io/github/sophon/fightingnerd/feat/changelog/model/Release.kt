package io.github.sophon.fightingnerd.feat.changelog.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class Release(
    val version: String = "",
    val isPreRelease: Boolean,
    val type: Type,
    val changeList: ImmutableList<String> = persistentListOf(),
) {
    enum class Type {
        BOT,
        APP,
    }
}
