package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikimizuumi.FEATURE_URL

internal fun String.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Character {
    val idName = this.cleanHtml().lowercase()
    val displayName = this.cleanHtml()
    val queryName = this.createQueryName()
    val game = Game.fromId(gameId)

    val char = Character(
        id = idName,
        displayName = displayName,
        queryName = queryName,
        aliasList = idName.createAliases(),
        wikiUrl = game?.wikiUrl ?: FEATURE_URL,
        images = Character.Images(
            iconUrl = imageUrlMap[queryName]
        )
    )

    return char
}

internal fun CharacterListResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): List<Character> {
    return cargoquery.map {
        val dto = it.title

        val character = Character(
            id = dto.chara.lowercase(),
            displayName = dto.chara,
            queryName = dto.chara,
            wikiUrl = dto.chara.formWikiUrl(gameId),
            aliasList = listOf(),
            images = Character.Images(
                iconUrl = imageUrlMap[it.title.chara],
            ),
            uni2Properties = Character.Uni2Properties(
                smartSteer = dto.smartSteer,
                hp = dto.health,
                fWalkSpeed = dto.fWalkSpeed?.cleanHtmlOrNull(),
                fWalkSpeedNote = dto.fWalkSpeedNote?.cleanHtmlOrNull(),
                bWalkSpeed = dto.bWalkSpeed?.cleanHtmlOrNull(),
                bWalkSpeedNote = dto.bWalkSpeedNote?.cleanHtmlOrNull(),
                jumpStartup = dto.jumpStartup?.cleanHtmlOrNull(),
                jumpDuration = dto.jumpDuration?.cleanHtmlOrNull(),
                jumpDurationNote = dto.jumpDurationNote?.cleanHtmlOrNull(),
                dashStartup = dto.dashStartup?.cleanHtmlOrNull(),
                iDashSpeed = dto.iDashSpeed?.cleanHtmlOrNull(),
                iDashSpeedNote = dto.iDashSpeedNote?.cleanHtmlOrNull(),
                dashAccel = dto.dashAccel?.cleanHtmlOrNull(),
                dashAccelNote = dto.dashAccelNote?.cleanHtmlOrNull(),
                maxDashSpeed = dto.maxDashSpeed?.cleanHtmlOrNull(),
                bDashStartup = dto.bDashStartup?.cleanHtmlOrNull(),
                bDashDuration = dto.bDashDuration?.cleanHtmlOrNull(),
                bDashDurationNote = dto.bDashDurationNote?.cleanHtmlOrNull(),
                bDashDistance = dto.bDashDistance?.cleanHtmlOrNull(),
                bDashDistanceNote = dto.bDashDistanceNote?.cleanHtmlOrNull(),
                bDashFullInvulStart = dto.bDashFullInvulStart?.cleanHtmlOrNull(),
                bDashFullInvulEnd = dto.bDashFullInvulEnd?.cleanHtmlOrNull(),
                bDashThrowInvulStart = dto.bDashThrowInvulStart?.cleanHtmlOrNull(),
                bDashThrowInvulEnd = dto.bDashThrowInvulEnd?.cleanHtmlOrNull(),
                throwWidth = dto.throwWidth?.cleanHtmlOrNull(),
                throwRange = dto.throwRange?.cleanHtmlOrNull(),
                trait = dto.trait?.cleanHtmlOrNull().formatBulletPoints(),
                vorpalTrait = dto.vorpalTrait?.cleanHtmlOrNull().formatBulletPoints(),
            ),
        )

        character
    }
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
    val meltyAliases = when (this.lowercase()) {
        "akiha tohno" -> listOf("akiha", "ak")
        "aoko aozaki" -> listOf("aoko", "aozaki", "ao")
        "arcueid brunestud" -> listOf("arcueid", "brunestud", "arc", "ar")
        "ciel" -> listOf("cl", "ci")
        "dead apostle noel" -> listOf("dead", "dan", "vnoel", "dn")
        "hisui" -> listOf("hi")
        "hisui & kohaku" -> listOf("maids", "hk")
        "kohaku" -> listOf("ko", "koha")
        "kouma kishima" -> listOf("kouma", "kishima", "ki")
        "mario" -> listOf("mario", "bestino", "ma")
        "mash kyrielight" -> listOf("mash", "kyrielight", "mas")
        "michael roa valdamjong" -> listOf("michael", "valdamjong", "roa", "ro")
        "miyako arima" -> listOf("miyako", "arima", "mi")
        "neco-arc" -> listOf("neco", "narc", "ne")
        "noel" -> listOf("no")
        "powered ciel" -> listOf("powered", "pciel", "pc")
        "red arcueid" -> listOf("red", "warc", "re")
        "saber" -> listOf("sa")
        "shiki tohno" -> listOf("shiki", "sh", "tony")
        "monte cristo" -> {
            listOf("cristo", "count", "dantes", "edmond", "ed")
        }
        "ushiwakamaru" -> listOf("ushi", "us")
        "vlov arkhangel" -> listOf("vlov", "arkhangel", "vl")

        else -> null
    }

    val aliases = this
        .split("-")
        .takeIf { it.size > 1 }
        ?.let { parts ->
            listOf(
                parts.last().lowercase(),
                parts.joinToString("") { it.first().lowercase() }
            )
        }

    return meltyAliases ?: aliases ?: listOf()
}

internal fun String.formWikiUrl(gameId: String): String {
    return Game.fromId(gameId)?.let { "${it.wikiUrl}/$this" } ?: ""
}

internal fun String?.formatBulletPoints(): String? {
    return this?.replace("*", "- ")
}
