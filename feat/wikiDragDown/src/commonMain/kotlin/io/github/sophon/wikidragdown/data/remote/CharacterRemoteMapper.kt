package io.github.sophon.wikidragdown.data.remote

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikidragdown.domain.BASE_URL
import io.github.sophon.wikidragdown.integration.model.Roa2Properties
import io.github.sophon.wikidragdown.util.formIcon

internal fun List<CharacterResponseDto>.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): List<Character> {
    val characterList = this.map { it.toDomain(imageUrlMap, gameId) }

    return characterList
}

internal fun CharacterResponseDto.toDomain(
    imageUrlMap: Map<String, String>,
    gameId: String,
): Character {
    val dto = this
    val id = chara.replace(" ", "_").lowercase()
    val aliases = chara.formAliases()
    val iconFilename = dto.chara.formIcon()

    val character = Character(
        id = id,
        displayName = dto.chara,
        remoteQueryId = dto.chara,
        aliasList = aliases,
        wikiUrl = dto.chara.formWikiUrl(gameId),
        images = Character.Images(
            iconId = iconFilename,
            iconUrl = imageUrlMap[iconFilename],
        ),
        gameProperties = Roa2Properties(
            dacusSpeedMultiplier = dto.dacusSpeedMultiplier?.toString(),
            weight = dto.weight?.toString(),
            frictionGround = dto.frictionGround?.toString(),
            frictionAir = dto.frictionAir?.toString(),
            dashFrames = dto.dashFrames?.toString(),
            dashSpeed = dto.dashSpeed?.toString(),
            dashAcceleration = dto.dashAcceleration?.toString(),
            runSpeedMax = dto.runSpeedMax?.toString(),
            runTurnAcceleration = dto.runTurnAcceleration?.toString(),
            runTurnFrames = dto.runTurnFrames?.toString(),
            walkAccelerationMax = dto.walkAccelerationMax?.toString(),
            walkSpeedMax = dto.walkSpeedMax?.toString(),
            gravity = dto.gravity?.toString(),
            hitstunGravity = dto.hitstunGravity?.toString(),
            fallSpeedMax = dto.fallSpeedMax?.toString(),
            fastFallSpeed = dto.fastFallSpeed?.toString(),
            airAcceleration = dto.airAcceleration?.toString(),
            airSpeedHorizontalMax = dto.airSpeedHorizontalMax?.toString(),
            jumpSpeedHorizontalMax = dto.jumpSpeedHorizontalMax?.toString(),
            fullHopSpeed = dto.fullHopSpeed?.toString(),
            shortHopSpeed = dto.shortHopSpeed?.toString(),
            doubleJumpSpeed = dto.doubleJumpSpeed?.toString(),
            doubleJumpMaxHorizontalSpeed = dto.doubleJumpMaxHorizontalSpeed?.toString(),
            airDodgeSpeed = dto.airDodgeSpeed?.toString(),
            airDodgeFriction = dto.airDodgeFriction?.toString(),
            rollSpeed = dto.rollSpeed?.toString(),
            shieldSizeMultiplier = dto.shieldSizeMultiplier?.toString(),
            ledgeStandSpeed = dto.ledgeStandSpeed?.toString(),
            ledgeRollSpeed = dto.ledgeRollSpeed?.toString(),
            ledgeJumpMaxHorizontalAirSpeed = dto.ledgeJumpMaxHorizontalAirSpeed?.toString(),
            getupRollSpeed = dto.getupRollSpeed?.toString(),
            techRollSpeed = dto.techRollSpeed?.toString(),
            wallJumpSpeedY = dto.wallJumpSpeedY?.toString(),
            wallJumpSpeedX = dto.wallJumpSpeedX?.toString(),
        )
    )

    return character
}

private fun String.formWikiUrl(gameId: String): String {
    val formatted = this.replace(" ", "_")
    val game = Game.fromId(gameId)
    val url = "${game?.wikiUrl ?: BASE_URL}/$formatted"
    return url
}

private fun String.formAliases(): List<String> {
    val id = this
        .lowercase()
        .split(" ")
        .takeLast(1)
    return id
}
