package io.github.sophon.wiki.adapter.outbound.ktor.superCombo

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.createAliases
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterGameProperties
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.gameProperties.MKCharProperties
import io.github.sophon.wiki.application.domain.model.gameProperties.SFCharProperties

internal fun SuperComboCharacterListResponseDto.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): List<Character> {
    val characterList = cargoquery
        .filterOutIrrelevant()
        .map { query -> formCharacter(query.title, game, imageUrlMap) }
    return characterList
}

/**
 * Not a `CharacterDto` extension - its `Character` field would shadow the `Character` model.
 */
private fun formCharacter(
    dto: CharacterDto,
    game: Game,
    imageUrlMap: Map<String, String>,
): Character {
    val character = Character(
        id = CharacterId(game, dto.chara),
        displayName = (dto.name ?: dto.chara),
        remoteQueryId = dto.chara,
        wikiUrl = "$WIKI_BASE_URL/${game.id}/${dto.chara}",
        aliasList = dto.chara.createAliases(addInitials = (game != Game.MK1)),
        images = Character.Images(
            iconId = dto.icon,
            iconUrl = dto.icon.let { imageUrlMap[it] },
            bannerUrl = dto.portrait.let { imageUrlMap[it] },
        ),
        hp = dto.hp,
        gameProperties = dto.toGameProperties(game),
    )
    return character
}

private fun CharacterDto.toGameProperties(game: Game): CharacterGameProperties? {
    val properties = when (game) {
        Game.MK1 -> MKCharProperties(
            hpMod = hpmod,
            throwDmg = throwdmg,
        )

        Game.StreetFighter6 -> SFCharProperties(
            fwdWalkSpd = fwdWalkSpd,
            bwdWalkSpd = bwdWalkSpd,
            fwdDashSpd = fwdDashSpd,
            bwdDashSpd = bwdDashSpd,
            fwdDashDist = fwdDashDist,
            bwdDashDist = bwdDashDist,
            dRushMin = dRushMin,
            dRushBlock = dRushBlock,
            dRushMax = dRushMax,
            throwRange = throwRange,
            throwHurtbox = throwHurtbox,
            jumpSpd = jumpSpd,
            jumpApex = jumpApex,
            fwdJumpDist = fwdJumpDist,
            bwdJumpDist = bwdJumpDist,
        )

        else -> null
    }
    return properties
}

/**
 * Drops MK1 Kameos and names with parentheses.
 */
private fun List<CargoQueryItem>.filterOutIrrelevant(): List<CargoQueryItem> {
    val filtered = this
        .filter { it.title.Character?.contains("(Kameo)") != true }
        .filter { it.title.chara.contains("(").not() }
    return filtered
}
