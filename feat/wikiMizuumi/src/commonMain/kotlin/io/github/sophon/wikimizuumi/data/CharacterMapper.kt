package io.github.sophon.wikimizuumi.data

import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikimizuumi.FEATURE_URL

internal fun String.toDomain(gameId: String): Character {
    val idName = this.cleanHtml().lowercase()
    val displayName = this.cleanHtml()
    val queryName = this.createQueryName()

    val char = Character(
        id = idName,
        displayName = displayName,
        queryName = queryName,
        aliasList = idName.createAliases(),
        wikiUrl = FEATURE_URL,
    )

    return char
}

internal fun CharacterListResponseDto.toDomain(
//    imageUrlMap: Map<String, String>,
    gameId: String,
): List<Character> {
    return cargoquery.map {
        val dto = it.title

        Character(
            id = dto.chara.lowercase(),
            displayName = dto.chara,
            queryName = dto.chara,
            wikiUrl = dto.chara.formWikiUrl(gameId),
            aliasList = listOf(),
//            images = Character.Images(
//                iconUrl = dto.icon.let { imageUrlMap[it] },
//                bannerUrl = dto.portrait.let { imageUrlMap[it] },
//            ),
            uni2Properties = Character.Uni2Properties(
                smartSteer = dto.smartSteer,
                hp = dto.health,
                fWalkSpeed = dto.fWalkSpeed,
                fWalkSpeedNote = dto.fWalkSpeedNote,
                bWalkSpeed = dto.bWalkSpeed,
                bWalkSpeedNote = dto.bWalkSpeedNote,
                jumpStartup = dto.jumpStartup,
                jumpDuration = dto.jumpDuration,
                jumpDurationNote = dto.jumpDurationNote,
                dashStartup = dto.dashStartup,
                iDashSpeed = dto.iDashSpeed,
                iDashSpeedNote = dto.iDashSpeedNote,
                dashAccel = dto.dashAccel,
                dashAccelNote = dto.dashAccelNote,
                maxDashSpeed = dto.maxDashSpeed,
                bDashStartup = dto.bDashStartup,
                bDashDuration = dto.bDashDuration,
                bDashDurationNote = dto.bDashDurationNote,
                bDashDistance = dto.bDashDistance,
                bDashDistanceNote = dto.bDashDistanceNote,
                bDashFullInvulStart = dto.bDashFullInvulStart,
                bDashFullInvulEnd = dto.bDashFullInvulEnd,
                bDashThrowInvulStart = dto.bDashThrowInvulStart,
                bDashThrowInvulEnd = dto.bDashThrowInvulEnd,
                throwWidth = dto.throwWidth,
                throwRange = dto.throwRange,
                trait = dto.trait,
                vorpalTrait = dto.vorpalTrait,
            ),
        )
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
    val aliases = when (this.lowercase()) {
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
        else -> listOf()
    }

    return aliases
}

internal fun String.formWikiUrl(gameId: String): String {
    return Game.fromId(gameId)?.let { "${it.wikiUrl}/$this" } ?: ""
}
