package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikimizuumi.FEATURE_URL

internal fun String.toDomain(gameId: String): Character {
    val idName = this.cleanHtml()
    val displayName = this.cleanHtml()
    val queryName = this.createQueryName()

    val char = Character(
        id = idName,
        displayName = displayName,
        queryName = queryName,
        aliasList = displayName.createAliases(),
        wikiUrl = "$FEATURE_URL/$gameId/$queryName",
    )

    return char
}

//TODO: this might be a core util
internal fun String.createQueryName(): String {
    return this
        .cleanHtml()
        .removeAccents()
        .split(' ')
        .joinToString("_")
}

internal fun String.createAliases(): List<String> {
    val aliases = when (this.lowercase()) {
        "akiha tohno" -> listOf("akiha", "ak")
        "aoko aozaki" -> listOf("aoko", "aozaki", "ao")
        "arcueid brunestud" -> listOf("arcueid", "brunestud", "arc", "ar")
        "ciel" -> listOf("cl", "ci")
        "dead apostle noel" -> listOf("dead", "dan", "vnoel", "dn")
        "hisui" -> listOf("hi")
        "hisui & kohaku" -> listOf("maids", "hk")
        "kohaku" -> listOf("ko")
        "kouma kishima" -> listOf("kouma", "kishima", "ki")
        "mario gallo bestino" -> listOf("mario", "bestino", "ma")
        "mash kyrielight" -> listOf("mash", "kyrielight", "mas")
        "michael roa valdamjong" -> listOf("michael", "valdamjong", "roa", "ro")
        "miyako arima" -> listOf("miyako", "arima", "mi")
        "neco-arc" -> listOf("neco", "arc", "ne")
        "noel" -> listOf("no")
        "powered ciel" -> listOf("powered", "pciel", "pc")
        "red arcueid" -> listOf("red", "warc", "ar", "re")
        "saber" -> listOf("sa")
        "shiki tohno" -> listOf("shiki", "sh")
        "the count of monte cristo" -> {
            listOf("cristo", "count", "dantes", "edmond", "ed")
        }
        "ushiwakamaru" -> listOf("ushi", "us")
        "vlov arkhangel" -> listOf("vlov", "arkhangel", "vl")
        else -> listOf()
    }

    return aliases
}
