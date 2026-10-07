package io.github.sophon.fightingnerd.app.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Release(
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
