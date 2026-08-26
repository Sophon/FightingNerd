package io.github.sophon.wikimizuumi.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.core.util.removeAccents
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikimizuumi.domain.FEATURE_URL
import io.github.sophon.wikimizuumi.integration.model.Uni2Properties

internal fun String.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): Character {
    val idName = this.cleanHtml().lowercase().replace(" ", "_")
    val displayName = this.cleanHtml()
    val queryName = this
    val game = Game.fromId(gameId)
    val aliasList = this.createAliases()
    val wikiUrl = game?.wikiUrl?.let { "${it}/${queryName.replace(" ", "_")}" }
    val iconKeys = when (game) {
        Game.MBTL -> listOf(idName.substringBefore("_"), idName.substringAfterLast("_"))
        else -> listOf(idName)
    }
    val iconId = iconKeys.firstOrNull { imageUrlMap.contains(it) }
    val iconUrl = iconKeys.firstNotNullOfOrNull { imageUrlMap[it] } ?: game?.iconUrl

    val char = Character(
        id = idName,
        displayName = displayName,
        remoteQueryId = queryName,
        aliasList = aliasList,
        wikiUrl = wikiUrl ?: FEATURE_URL,
        images = Character.Images(
            iconId = iconId,
            iconUrl = iconUrl,
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

        val gameProperties = when (Game.fromId(gameId)) {
            Game.Uni2 -> {
                Uni2Properties(
                    smartSteer = dto.smartSteer,
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
                )
            }
            else -> null
        }

        val character = Character(
            id = dto.chara.lowercase(),
            displayName = dto.chara,
            remoteQueryId = dto.chara,
            wikiUrl = dto.chara.formWikiUrl(gameId),
            aliasList = listOf(),
            images = Character.Images(
                iconId = it.title.chara,
                iconUrl = imageUrlMap[it.title.chara],
            ),
            hp = dto.health,
            gameProperties = gameProperties,
        )

        character
    }
}


@Suppress("CyclomaticComplexMethod")
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

        else -> listOf()
    }

    val vsavAliases = when (this.lowercase()) {
        "anakaris" -> listOf("an")
        "aulbath" -> listOf("au")
        "bishamon" -> listOf("bi")
        "bulleta" -> listOf("bu")
        "demitri" -> listOf("de")
        "felicia" -> listOf("fe")
        "gallon" -> listOf("ga")
        "jedah" -> listOf("je")
        "lei-lei" -> listOf("le")
        "lilith" -> listOf("li")
        "morrigan" -> listOf("mo")
        "q-bee" -> listOf("qb")
        "sasquatch" -> listOf("sa")
        "victor" -> listOf("vi")
        "zabel" -> listOf("za")

        else -> listOf()
    }

    val combined = buildList {
        addAll(meltyAliases)
        addAll(vsavAliases)
    }.distinct()

    return combined
}

internal fun String.formWikiUrl(gameId: String): String {
    val url = Game.fromId(gameId)?.let { "${it.wikiUrl}/$this" } ?: ""
    return url
}

internal fun String?.formatBulletPoints(): String? {
    return this?.replace("*", "- ")
}
