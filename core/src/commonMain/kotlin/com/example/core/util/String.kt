package com.example.core.util

fun String.removeWhiteSpace(): String {
    return this.replace("\\s".toRegex(), "")
}

fun String.dropFirstAndJoin(delimiter: Char): String {
    return this
        .split(delimiter)
        .drop(1)
        .joinToString(delimiter.toString())
}

fun String.isAtLeast(wordCount: Int): Boolean {
    return this
        .split(' ')
        .size >= wordCount
}

fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) {
        take(maxLength - 3) + "..."
    } else this
}