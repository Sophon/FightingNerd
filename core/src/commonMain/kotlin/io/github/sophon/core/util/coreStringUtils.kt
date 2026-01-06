package io.github.sophon.core.util

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

fun String.decodeHtmlEntities(): String {
    return this
        // Decode &amp; FIRST so double-encoded entities work
        .replace("&amp;", "&")
        // NOW handle numeric entities
        .replace(Regex("&#(\\d+);")) { matchResult ->
            matchResult.groupValues[1].toInt().toChar().toString()
        }
        .replace(Regex("&#x([0-9A-Fa-f]+);")) { matchResult ->
            matchResult.groupValues[1].toInt(16).toChar().toString()
        }
        // Then other named entities
        .replace("&gt;", ">")
        .replace("&lt;", "<")
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&nbsp;", " ")
        .replace("&apos;", "'")
}

fun String?.orDash(): String = this?.takeUnless { it.isBlank() } ?: "-"

fun String.cleanHtml(): String {
    return this
        .decodeHtmlEntities()
        .removeHtmlTags()
        .replace("'''", "") //wiki bolt
        .replace(Regex("\\*\\s*\\n"), "* ")
        .trim()
}

// <br> & <br/>
internal fun String.removeHtmlTags(): String {
    return this
        .replace(Regex("<br\\s*/?>"), "\n")
        .replace(Regex("<[^>]*>"), "")
}

// LOL TODO: find a library
fun String.removeAccents(): String {
    val accentsMap = mapOf(
        'á' to 'a', 'à' to 'a', 'â' to 'a', 'ä' to 'a', 'ã' to 'a', 'å' to 'a', 'ā' to 'a',
        'é' to 'e', 'è' to 'e', 'ê' to 'e', 'ë' to 'e', 'ē' to 'e', 'ė' to 'e', 'ę' to 'e',
        'í' to 'i', 'ì' to 'i', 'î' to 'i', 'ï' to 'i', 'ī' to 'i', 'į' to 'i',
        'ó' to 'o', 'ò' to 'o', 'ô' to 'o', 'ö' to 'o', 'õ' to 'o', 'ō' to 'o', 'ø' to 'o',
        'ú' to 'u', 'ù' to 'u', 'û' to 'u', 'ü' to 'u', 'ū' to 'u', 'ů' to 'u',
        'ý' to 'y', 'ÿ' to 'y',
        'ñ' to 'n', 'ň' to 'n', 'ń' to 'n',
        'ç' to 'c', 'č' to 'c', 'ć' to 'c',
        'ď' to 'd', 'đ' to 'd',
        'ř' to 'r', 'ŕ' to 'r',
        'š' to 's', 'ś' to 's',
        'ť' to 't',
        'ž' to 'z', 'ź' to 'z', 'ż' to 'z',
        'ľ' to 'l', 'ł' to 'l',
        // Uppercase variants
        'Á' to 'A', 'À' to 'A', 'Â' to 'A', 'Ä' to 'A', 'Ã' to 'A', 'Å' to 'A', 'Ā' to 'A',
        'É' to 'E', 'È' to 'E', 'Ê' to 'E', 'Ë' to 'E', 'Ē' to 'E', 'Ė' to 'E', 'Ę' to 'E',
        'Í' to 'I', 'Ì' to 'I', 'Î' to 'I', 'Ï' to 'I', 'Ī' to 'I', 'Į' to 'I',
        'Ó' to 'O', 'Ò' to 'O', 'Ô' to 'O', 'Ö' to 'O', 'Õ' to 'O', 'Ō' to 'O', 'Ø' to 'O',
        'Ú' to 'U', 'Ù' to 'U', 'Û' to 'U', 'Ü' to 'U', 'Ū' to 'U', 'Ů' to 'U',
        'Ý' to 'Y', 'Ÿ' to 'Y',
        'Ñ' to 'N', 'Ň' to 'N', 'Ń' to 'N',
        'Ç' to 'C', 'Č' to 'C', 'Ć' to 'C',
        'Ď' to 'D', 'Đ' to 'D',
        'Ř' to 'R', 'Ŕ' to 'R',
        'Š' to 'S', 'Ś' to 'S',
        'Ť' to 'T',
        'Ž' to 'Z', 'Ź' to 'Z', 'Ż' to 'Z',
        'Ľ' to 'L', 'Ł' to 'L'
    )

    return map { accentsMap[it] ?: it }.joinToString("")
}

fun String.maskSecret(): String {
    return when {
        isEmpty() -> ""
        length == 1 -> "*"
        (length <= 8) -> {
            "${first()}${"*".repeat(length - 2)}${last()}"
        }
        else -> {
            "${take(4)}${"*".repeat(length - 8)}${takeLast(4)}"
        }
    }
}

fun String.createAliases(): List<String> {
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
                add(joinToString("") { it.first().lowercase() })
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

fun String.capitalize(): String = replaceFirstChar { it.uppercase() }

fun String.extractFirstWord(): String {
    return this
        .trim()
        .substringBefore(' ')
}