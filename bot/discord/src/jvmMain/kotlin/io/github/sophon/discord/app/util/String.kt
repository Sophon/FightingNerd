package io.github.sophon.discord.app.util

internal fun String.removeTag(): String {
    return if (contains("@")) {
        this
            .substringAfter("@")
            .substringAfter(" ")
    } else this
}

