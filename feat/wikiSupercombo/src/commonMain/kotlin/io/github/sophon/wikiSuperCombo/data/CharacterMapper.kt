package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiSuperCombo.WIKI_BASE_URL

internal fun CharacterListResponseDto.toDomain(): List<Character> {
    return cargoquery.map { dto ->
        val charDto = dto.title
        Character(
            id = charDto.character.createId(),
            displayName = charDto.name,
            wikiUrl = createWikiUrlFrom(name = charDto.chara),
            aliasList = charDto.chara.createAliases(),
            images = Character.Images(
                iconUrl = charDto.icon,
                bannerUrl = charDto.portrait,
            ),
            sf6Properties = Character.SF6Properties(
                fwdWalkSpd = charDto.fwdWalkSpd,
                bwdWalkSpd = charDto.bwdWalkSpd,
                fwdDashSpd = charDto.fwdDashSpd,
                bwdDashSpd = charDto.bwdDashSpd,
                fwdDashDist = charDto.fwdDashDist,
                bwdDashDist = charDto.bwdDashDist,
                dRushMin = charDto.dRushMin,
                dRushBlock = charDto.dRushBlock,
                dRushMax = charDto.dRushMax,
                throwRange = charDto.throwRange,
                throwHurtbox = charDto.throwHurtbox,
                jumpSpd = charDto.jumpSpd,
                jumpApex = charDto.jumpApex,
                fwdJumpDist = charDto.fwdJumpDist,
                bwdJumpDist = charDto.bwdJumpDist,
                hp = charDto.hp,
            )
        )
    }
}

/**
 * Result:
 * sf6-chun_li, sf6-m_bison, sf6-dee_jay etc
 */
private fun String.createId(): String {
    val parts = split("/").dropLast(1)
//    val gameInitials = parts.first()
//        .split(" ")
//        .joinToString("") { it.first().lowercase() }
    val charId = parts.last()
        .replace(".", "")
        .replace("-", "_")
        .split(" ")
        .joinToString("_") { it.lowercase() }

    return charId
}

private fun createWikiUrlFrom(name: String): String {
    return "$WIKI_BASE_URL/$name"
}

private fun String.createAliases(): List<String> {
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
                if (words.size == 2) {
                    add(words.last().lowercase())
                }
            }
    }
}
