package io.github.sophon.core.util

fun String.createAliases(addInitials: Boolean = true): List<String> {
    return buildList {
        split(
            " ",
            "-",
            "_",
            ".",
        )
            .filter { it.isNotBlank() }
            .takeIf { it.size > 1 }
            ?.apply {
                if (addInitials) {
                    add(joinToString("") { it.first().lowercase() })
                }
            }
            ?.let { words ->
                if (words.first().length >= 2) {
                    add(words.first().lowercase())
                }
                if (words.size >= 2 && words.last().length >= 2) {
                    add(words.last().lowercase())
                }
            }
    }.distinct()
}