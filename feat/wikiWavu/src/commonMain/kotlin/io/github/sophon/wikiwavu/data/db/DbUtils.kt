package io.github.sophon.wikiwavu.data.db

internal fun List<String>.fromDomain(): String {
    val joined = joinToString(LIST_DELIMITER)
    return joined
}

internal fun String.toDomain(): List<String> {
    if (isEmpty()) {
        return emptyList()
    }
    val parts = split(LIST_DELIMITER)
    return parts
}

private const val LIST_DELIMITER = ";"
