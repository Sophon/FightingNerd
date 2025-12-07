package io.github.sophon.discord.util

internal fun String.removeTag(): String {
    return this
        .substringAfter("@")
        .substringAfter(" ")
}

