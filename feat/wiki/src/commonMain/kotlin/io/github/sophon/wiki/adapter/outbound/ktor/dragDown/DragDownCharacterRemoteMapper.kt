package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.gameProperties.Roa2CharProperties
import io.github.sophon.wiki.application.domain.model.wiki.Game

internal fun List<DragDownCharacterResponseDto>.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): List<Character> {
    val characterList = map { dto -> dto.toDomain(game, imageUrlMap) }
    return characterList
}

/**
 * DragDown only serves RoA2 - the portrait file name follows its naming.
 */
internal fun String.formIconFileName(): String {
    val fileName = "RoA2_${replace(" ", "_")}_Portrait.png"
    return fileName
}

private fun DragDownCharacterResponseDto.toDomain(
    game: Game,
    imageUrlMap: Map<String, String>,
): Character {
    val iconFileName = chara.formIconFileName()

    val character = Character(
        id = CharacterId(game, chara),
        displayName = chara,
        remoteQueryId = chara,
        aliasList = chara.formAliases(),
        wikiUrl = "${game.wikiUrl}/${chara.replace(" ", "_")}",
        images = Character.Images(
            iconId = iconFileName,
            iconUrl = imageUrlMap[iconFileName],
        ),
        gameProperties = toGameProperties(),
    )
    return character
}

private fun DragDownCharacterResponseDto.toGameProperties(): Roa2CharProperties {
    val properties = Roa2CharProperties(
        dacusSpeedMultiplier = dacusSpeedMultiplier?.toString(),
        weight = weight?.toString(),
        frictionGround = frictionGround?.toString(),
        frictionAir = frictionAir?.toString(),
        dashFrames = dashFrames?.toString(),
        dashSpeed = dashSpeed?.toString(),
        dashAcceleration = dashAcceleration?.toString(),
        runSpeedMax = runSpeedMax?.toString(),
        runTurnAcceleration = runTurnAcceleration?.toString(),
        runTurnFrames = runTurnFrames?.toString(),
        walkAccelerationMax = walkAccelerationMax?.toString(),
        walkSpeedMax = walkSpeedMax?.toString(),
        gravity = gravity?.toString(),
        hitstunGravity = hitstunGravity?.toString(),
        fallSpeedMax = fallSpeedMax?.toString(),
        fastFallSpeed = fastFallSpeed?.toString(),
        airAcceleration = airAcceleration?.toString(),
        airSpeedHorizontalMax = airSpeedHorizontalMax?.toString(),
        jumpSpeedHorizontalMax = jumpSpeedHorizontalMax?.toString(),
        fullHopSpeed = fullHopSpeed?.toString(),
        shortHopSpeed = shortHopSpeed?.toString(),
        doubleJumpSpeed = doubleJumpSpeed?.toString(),
        doubleJumpMaxHorizontalSpeed = doubleJumpMaxHorizontalSpeed?.toString(),
        airDodgeSpeed = airDodgeSpeed?.toString(),
        airDodgeFriction = airDodgeFriction?.toString(),
        rollSpeed = rollSpeed?.toString(),
        shieldSizeMultiplier = shieldSizeMultiplier?.toString(),
        ledgeStandSpeed = ledgeStandSpeed?.toString(),
        ledgeRollSpeed = ledgeRollSpeed?.toString(),
        ledgeJumpMaxHorizontalAirSpeed = ledgeJumpMaxHorizontalAirSpeed?.toString(),
        getupRollSpeed = getupRollSpeed?.toString(),
        techRollSpeed = techRollSpeed?.toString(),
        wallJumpSpeedY = wallJumpSpeedY?.toString(),
        wallJumpSpeedX = wallJumpSpeedX?.toString(),
    )
    return properties
}

/**
 * The last word of the name - `La Reina` → `reina`.
 */
private fun String.formAliases(): List<String> {
    val aliases = lowercase()
        .split(" ")
        .takeLast(1)
    return aliases
}
