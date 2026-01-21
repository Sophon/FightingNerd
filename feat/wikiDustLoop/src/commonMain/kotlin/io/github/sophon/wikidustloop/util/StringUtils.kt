package io.github.sophon.wikidustloop.util

import io.github.sophon.wikidustloop.WIKI_BASE_URL

fun String?.toClickable(): String? {
    if (this == null) return null

    val regex = """\[\[(.*?)\]\]""".toRegex() // "text text [[match]] text [[match]] text.

    val transformed = regex.replace(this) { matchResult ->
        val content = matchResult.groupValues[1]
        val fields = content.split("|")
        val title = fields.lastOrNull() ?: ""
        val partialUrl = fields.firstOrNull()
            .orEmpty()
            .replace(" ", "_")
            .trim()
        "[$title](${WIKI_BASE_URL}/$partialUrl)"
    }

    return transformed
}