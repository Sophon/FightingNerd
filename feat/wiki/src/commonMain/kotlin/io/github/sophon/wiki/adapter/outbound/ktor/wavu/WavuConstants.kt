package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.wiki.model.wiki.Game

internal const val URL_PREFIX_VIDEO = "https://wavu.wiki/t/Special:Redirect/file/"
internal const val URL_PREFIX_MOVE = "https://wavu.wiki/t"

/**
 * Tekken 8 pages are bare (`Jin`, `Jin_movelist`), older games append the game (`Jin_(Tag_2)`, `Jin_movelist_(Tag_2)`).
 */
internal fun Game.toPageSuffix(): String {
    val suffix = when (this) {
        Game.Tag2 -> "_(Tag_2)"
        else -> ""
    }
    return suffix
}
