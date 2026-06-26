package io.github.sophon.discord.util

internal fun String.removeTag(): String {
    return if (contains("@")) {
        this
            .substringAfter("@")
            .substringAfter(" ")
    } else this
}

