package io.github.sophon.botdiscord.util

internal fun String.removeTag(): String {
    return this
        .substringAfter("@")
        .substringAfter(" ")
}

internal fun String.replaceItalic(): String {
    return this
        .replace("<em>", "*")
        .replace("</em>", "*")
}

/**
 * Replaces HTML tags with Markdown.
 *
 * Examples:
 * - `!<'block'>` → `__block__`
 * - `!<'whiff punish','whiff'>` → `__whiff__`
 *
 * Takes the last comma-separated value if multiple are present.
 */
internal fun String.replaceUnderline(): String {
    return this.replace(Regex("!<'([^']+)'(?:,'[^']*')*>")) { matchResult ->
        val content = matchResult.groupValues[1]
        val words = content.split("','")
        val lastWord = words.last()
        "**__${lastWord}__**"
    }
}