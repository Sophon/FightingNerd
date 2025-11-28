package io.github.sophon.discord.util

/**
 * [[Kazuya combos#Staples|+59a]] -> [+59a](https://wavu.wiki/t/Kazuya_combos#Staples)
 *
 * Heat Dash +18g, [[Reina_combos#Mini-combos|+43a (+35)]] -> Heat Dash +18g, [+43a (+35)](https://wavu.wiki/t/Reina_combos#Mini-combos)
 */
internal fun String?.orClickable(): String? {
    if (
        this == null
        || contains("[[").not()
        || contains("]]").not()
        || contains("|").not()
        || contains("#").not()
    ) return this

    val precedingText = if (startsWith("[[")) "" else substringBefore("[[")
    val text = substringAfter("|").substringBefore("]]")
    val section = substringAfter("[[")
        .substringBefore("|")
        .replace(" ", "_")
    val trailingText = if (contains("]]")) substringAfter("]]") else ""

    val url = BASE_URL + section

    return "$precedingText[$text]($url)$trailingText"
}

private const val BASE_URL = "https://wavu.wiki/t/"