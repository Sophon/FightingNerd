package io.github.sophon.glossaryinfil.data

import io.github.sophon.core.util.urlEncode
import io.github.sophon.glossaryinfil.FEATURE_URL
import io.github.sophon.glossaryinfil.domain.GlossaryItem

internal fun GlossaryItemDto.toDomain(): GlossaryItem {
    return GlossaryItem(
        term = term,
        definition = def.toMarkdown(),
        altTerm = altterm.orEmpty(),
        video = video ?: listOf(),
        imageUrl = image.toUrl(term),
        games = games ?: listOf(),
        jpTranslation = jp
            ?.split("<br>")
            ?.map { it.replaceItalic().toMarkdown() }
            ?: listOf()
    )
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
internal fun String.toMarkdown(): String {
    var result = this
        .replace("<em>", "*")
        .replace("</em>", "*")
        .replace("<br>", "\n")

    // Process !<'...'> patterns
    while (true) {
        val startIndex = result.indexOf("!<'")
        if (startIndex == -1) break

        val endIndex = result.indexOf("'>", startIndex)
        if (endIndex == -1) break

        // Extract the full pattern and its content
        val fullPattern = result.substring(startIndex, endIndex + 2)
        val content = result.substring(startIndex + 3, endIndex)

        // Get the last comma-separated value
        val firstWord = content.split("','").first()
        val replacement = "**__${firstWord}__**"

        result = result.replaceFirst(fullPattern, replacement)
    }

    // Process ?<'url','text'> patterns (markdown links)
    while (true) {
        val startIndex = result.indexOf("?<'")
        if (startIndex == -1) break

        val endIndex = result.indexOf("'>", startIndex)
        if (endIndex == -1) break

        val fullPattern = result.substring(startIndex, endIndex + 2)
        val content = result.substring(startIndex + 3, endIndex)

        val parts = content.split("','")
        val url = parts.getOrNull(0) ?: ""
        val linkText = parts.getOrNull(1) ?: url

        val replacement = "[**${linkText}**](${url})"
        result = result.replaceFirst(fullPattern, replacement)
    }

    return result
}

internal fun List<String>?.toUrl(term: String): String? {
    if (this == null || size < 2) return null

    val extension = first()
    val fileName = term.urlEncode()

    return "$FEATURE_URL/images/terms/$fileName.${extension}"
}