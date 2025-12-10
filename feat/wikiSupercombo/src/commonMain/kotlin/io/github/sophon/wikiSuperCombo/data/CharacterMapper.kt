package io.github.sophon.wikiSuperCombo.data

import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.wikiSuperCombo.WIKI_BASE_URL

internal fun CharacterListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): List<Character> {
    val characterList = cargoquery
        .map { dto ->
            val charDto = dto.title
            Character(
                id = charDto.Character.createId(),
                displayName = charDto.name,
                queryName = charDto.chara,
                wikiUrl = createWikiUrlFrom(gameId, charDto.chara),
                aliasList = charDto.chara.createAliases(),
                images = Character.Images(
                    iconUrl = charDto.icon.let { imageUrlMap[it] },
                    bannerUrl = charDto.portrait.let { imageUrlMap[it] },
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
                ),
                mkProperties = Character.MortalKombatProperties(
                    hp = charDto.hp,
                    hpMod = charDto.hpmod,
                    throwDmg = charDto.throwdmg,
                )
            )
        }
        .filterOutKameos()

    return characterList
}

/**
 * Result:
 * sf6-chun_li, sf6-m_bison, sf6-dee_jay etc
 */
private fun String?.createId(): String {
    if (this == null) return "NULL"

    val parts = split("/").dropLast(1)
    val charId = parts.last()
        .replace(".", "")
        .replace("-", "_")
        .split(" ")
        .joinToString("_") { it.lowercase() }

    return charId
}

private fun createWikiUrlFrom(gameId: String, name: String): String {
    return "$WIKI_BASE_URL/$gameId/$name"
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

internal fun List<Character>.filterOutKameos(): List<Character> {
    return filterNot { it.displayName.contains("(Kameo)") }
}

