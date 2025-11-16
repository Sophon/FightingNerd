package io.github.sophon.fightingnerd.screens.home.data

import io.github.sophon.core.wiki.domain.model.Character

internal fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        displayName = displayName,
        queryName = queryName,
        wikiUrl = wikiUrl,
        aliases = aliasList.joinToString(";"),
        imageIconUrl = images?.iconUrl,
        imageBannerUrl = images?.bannerUrl,

        sf6FwdWalkSpd = sf6Properties?.fwdWalkSpd,
        sf6BwdWalkSpd = sf6Properties?.bwdWalkSpd,
        sf6FwdDashSpd = sf6Properties?.fwdDashSpd,
        sf6BwdDashSpd = sf6Properties?.bwdDashSpd,
        sf6FwdDashDist = sf6Properties?.fwdDashDist,
        sf6BwdDashDist = sf6Properties?.bwdDashDist,
        sf6DRushMin = sf6Properties?.dRushMin,
        sf6DRushBlock = sf6Properties?.dRushBlock,
        sf6DRushMax = sf6Properties?.dRushMax,
        sf6Hp = sf6Properties?.hp,
        sf6ThrowRange = sf6Properties?.throwRange,
        sf6ThrowHurtbox = sf6Properties?.throwHurtbox,
        sf6JumpSpd = sf6Properties?.jumpSpd,
        sf6JumpApex = sf6Properties?.jumpApex,
        sf6FwdJumpDist = sf6Properties?.fwdJumpDist,
        sf6BwdJumpDist = sf6Properties?.bwdJumpDist,
    )
}

internal fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        displayName = displayName,
        queryName = queryName,
        wikiUrl = wikiUrl,
        aliasList = aliases.orEmpty().split(";").filter { it.isNotBlank() },
        images = Character.Images(
            iconUrl = imageIconUrl,
            bannerUrl = imageBannerUrl,
        ),
        sf6Properties = if (sf6FwdWalkSpd != null) {
            Character.SF6Properties(
                fwdWalkSpd = sf6FwdWalkSpd,
                bwdWalkSpd = sf6BwdWalkSpd ?: "",
                fwdDashSpd = sf6FwdDashSpd ?: "",
                bwdDashSpd = sf6BwdDashSpd ?: "",
                fwdDashDist = sf6FwdDashDist ?: "",
                bwdDashDist = sf6BwdDashDist ?: "",
                dRushMin = sf6DRushMin ?: "",
                dRushBlock = sf6DRushBlock ?: "",
                dRushMax = sf6DRushMax ?: "",
                hp = sf6Hp ?: "",
                throwRange = sf6ThrowRange ?: "",
                throwHurtbox = sf6ThrowHurtbox ?: "",
                jumpSpd = sf6JumpSpd ?: "",
                jumpApex = sf6JumpApex ?: "",
                fwdJumpDist = sf6FwdJumpDist ?: "",
                bwdJumpDist = sf6BwdJumpDist ?: "",
            )
        } else null
    )
}