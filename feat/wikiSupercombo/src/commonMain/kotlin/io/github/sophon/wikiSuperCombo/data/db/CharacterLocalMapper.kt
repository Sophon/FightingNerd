package io.github.sophon.wikiSuperCombo.data.db

import io.github.sophon.core.wiki.data.toDomain
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiSuperCombo.data.SelectMK1ForGame
import io.github.sophon.wikiSuperCombo.data.SelectSF6ForGame
import io.github.sophon.wikiSuperCombo.integration.model.MK1Properties
import io.github.sophon.wikiSuperCombo.integration.model.SF6Properties

internal fun SelectSF6ForGame.toDomain(): Character {
    val character = Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconId = imagesIconId,
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
        gameProperties = SF6Properties(
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
        ),
    )
    return character
}

internal fun SelectMK1ForGame.toDomain(): Character {
    val character = Character(
        id = id,
        displayName = displayName,
        remoteQueryId = remoteQueryId,
        wikiUrl = wikiUrl,
        aliasList = aliases.toDomain(),
        images = Character.Images(
            iconId = imagesIconId,
            iconUrl = imagesIconUrl,
            bannerUrl = imagesBannerUrl,
        ),
        hp = hp,
        umo = umo.toDomain(),
        gameProperties = MK1Properties(
            hpMod = hpMod,
            throwDmg = throwDmg,
        ),
    )
    return character
}
