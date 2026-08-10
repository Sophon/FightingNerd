package io.github.sophon.core.wiki.data

fun List<String>.fromDomain(): String {
    val joined = joinToString(LIST_DELIMITER)
    return joined
}

fun String.toDomain(): List<String> {
    if (isEmpty()) {
        return emptyList()
    }
    val parts = split(LIST_DELIMITER)
    return parts
}

private const val LIST_DELIMITER = ";"
