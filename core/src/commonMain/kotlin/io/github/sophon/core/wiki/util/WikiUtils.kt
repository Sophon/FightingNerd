package io.github.sophon.core.wiki.util

internal fun String.normalizeForMatch(): String {
    val normalized = replace(" ", "").lowercase()
    return normalized
}
