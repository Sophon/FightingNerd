package io.github.sophon.wikiSuperCombo.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.createAliases
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterGameProperties
import io.github.sophon.wikiSuperCombo.domain.WIKI_BASE_URL
import io.github.sophon.wikiSuperCombo.integration.model.MK1Properties
import io.github.sophon.wikiSuperCombo.integration.model.SF6Properties
import kotlin.collections.get

internal fun CharacterListResponseDto.toDomain(
    gameId: String,
    imageUrlMap: Map<String, String>,
): List<Character> {
    val game = Game.fromId(gameId)

    val characterList = cargoquery
        .map { dto ->
            val charDto = dto.title

            val gameProperties: CharacterGameProperties? = when (game) {
                Game.MK1 -> {
                    MK1Properties(
                        hpMod = charDto.hpmod,
                        throwDmg = charDto.throwdmg,
                    )
                }
                Game.StreetFighter6 -> {
                    SF6Properties(
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
                    )
                }
                else -> {
                    null
                }
            }

            Character(
                id = charDto.Character.createId(),
                displayName = charDto.name ?: charDto.chara,
                remoteQueryId = charDto.chara,
                wikiUrl = createWikiUrlFrom(gameId, charDto.chara),
                aliasList = charDto.chara.createAliases(addInitials = (game != Game.MK1)),
                images = Character.Images(
                    iconId = charDto.icon,
                    iconUrl = charDto.icon.let { imageUrlMap[it] },
                    bannerUrl = charDto.portrait.let { imageUrlMap[it] },
                ),
                hp = charDto.hp,
                gameProperties = gameProperties,
            )
        }

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

