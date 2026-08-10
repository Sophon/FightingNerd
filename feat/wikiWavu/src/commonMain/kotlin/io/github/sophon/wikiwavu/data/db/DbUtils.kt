package io.github.sophon.wikiwavu.data.db

internal fun List<String>.fromDomain(): String {
    return joinToString(LIST_DELIMITER)
}

internal fun String.toDomain(): List<String> {
    if (isEmpty()) {
        return emptyList()
    }
    return split(LIST_DELIMITER)
}

private const val LIST_DELIMITER = ";"
