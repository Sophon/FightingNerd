package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.cleanHtml
import io.github.sophon.core.util.cleanHtmlOrNull
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.gameProperties.Uni2CharProperties

/**
 * Bulk games have no character table - the character is built from the move table's `chara` column.
 */
internal fun String.toDomain(
    game: Game,
    iconUrlMap: Map<String, String>,
): Character {
    val iconName = this.cleanHtml().lowercase().replace(" ", "_")
    val iconKeys = when (game) {
        Game.MBTL -> listOf(iconName.substringBefore("_"), iconName.substringAfterLast("_"))
        else -> listOf(iconName)
    }

    val character = Character(
        id = CharacterId(this),
        displayName = this.cleanHtml(),
        remoteQueryId = this,
        wikiUrl = "${game.wikiUrl}/${this.replace(" ", "_")}",
        aliasList = this.createAliases(),
        images = Character.Images(
            iconId = iconKeys.firstOrNull { iconUrlMap.contains(it) },
            iconUrl = (iconKeys.firstNotNullOfOrNull { iconUrlMap[it] } ?: game.iconUrl),
        ),
    )
    return character
}

internal fun MizuumiCharacterListResponseDto.toDomain(
    game: Game,
    iconUrlMap: Map<String, String>,
): List<Character> {
    val characterList = cargoquery.map { characterTitle ->
        val dto = characterTitle.title

        val character = Character(
            id = CharacterId(dto.chara),
            displayName = dto.chara,
            remoteQueryId = dto.chara,
            wikiUrl = "${game.wikiUrl}/${dto.chara}",
            images = Character.Images(
                iconId = dto.chara,
                iconUrl = iconUrlMap[dto.chara],
            ),
            hp = dto.health,
            gameProperties = dto.toGameProperties(game),
        )
        character
    }
    return characterList
}

private fun UniCharacterDto.toGameProperties(game: Game): CharacterGameProperties? {
    val properties = when (game) {
        Game.Uni2 -> Uni2CharProperties(
            smartSteer = smartSteer,
            fWalkSpeed = fWalkSpeed?.cleanHtmlOrNull(),
            fWalkSpeedNote = fWalkSpeedNote?.cleanHtmlOrNull(),
            bWalkSpeed = bWalkSpeed?.cleanHtmlOrNull(),
            bWalkSpeedNote = bWalkSpeedNote?.cleanHtmlOrNull(),
            jumpStartup = jumpStartup?.cleanHtmlOrNull(),
            jumpDuration = jumpDuration?.cleanHtmlOrNull(),
            jumpDurationNote = jumpDurationNote?.cleanHtmlOrNull(),
            dashStartup = dashStartup?.cleanHtmlOrNull(),
            iDashSpeed = iDashSpeed?.cleanHtmlOrNull(),
            iDashSpeedNote = iDashSpeedNote?.cleanHtmlOrNull(),
            dashAccel = dashAccel?.cleanHtmlOrNull(),
            dashAccelNote = dashAccelNote?.cleanHtmlOrNull(),
            maxDashSpeed = maxDashSpeed?.cleanHtmlOrNull(),
            bDashStartup = bDashStartup?.cleanHtmlOrNull(),
            bDashDuration = bDashDuration?.cleanHtmlOrNull(),
            bDashDurationNote = bDashDurationNote?.cleanHtmlOrNull(),
            bDashDistance = bDashDistance?.cleanHtmlOrNull(),
            bDashDistanceNote = bDashDistanceNote?.cleanHtmlOrNull(),
            bDashFullInvulStart = bDashFullInvulStart?.cleanHtmlOrNull(),
            bDashFullInvulEnd = bDashFullInvulEnd?.cleanHtmlOrNull(),
            bDashThrowInvulStart = bDashThrowInvulStart?.cleanHtmlOrNull(),
            bDashThrowInvulEnd = bDashThrowInvulEnd?.cleanHtmlOrNull(),
            throwWidth = throwWidth?.cleanHtmlOrNull(),
            throwRange = throwRange?.cleanHtmlOrNull(),
            trait = trait?.cleanHtmlOrNull()?.formatBulletPoints(),
            vorpalTrait = vorpalTrait?.cleanHtmlOrNull()?.formatBulletPoints(),
        )

        else -> null
    }
    return properties
}

@Suppress("CyclomaticComplexMethod")
private fun String.createAliases(): List<String> {
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
        "monte cristo" -> listOf("cristo", "count", "dantes", "edmond", "ed")
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

    val combined = (meltyAliases + vsavAliases).distinct()
    return combined
}

private fun String.formatBulletPoints(): String {
    val formatted = replace("*", "- ")
    return formatted
}
