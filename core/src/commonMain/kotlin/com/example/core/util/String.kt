package com.example.core.util

import io.ktor.http.encodeURLParameter

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
    if (isBlank()) return wordCount == 0

    return this
        .split(' ')
        .filter { it.isNotBlank() }
        .size >= wordCount
}

fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) {
        take(maxLength - 3) + "..."
    } else this
}

fun String.urlEncode(): String = encodeURLParameter()

fun String?.orDash(): String = this ?: "-"

fun String.cleanHtml(): String {
    return this
        .decodeHtmlEntities()
        .removeHtmlTags()
        .replace(Regex("\\*\\s*\\n"), "* ")
        .trim()
}

private fun String.decodeHtmlEntities(): String {
    return this
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&amp;", "&")
        .replace("&nbsp;", " ")
        .replace("&#039;", "'")
        .replace("&apos;", "'")
}

internal fun String.removeHtmlTags(): String {
    return this.replace(Regex("<[^>]*>"), "")
}
