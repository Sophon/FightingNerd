package io.github.sophon.wikidragdown.data.remote

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import kotlin.test.Test

internal class CharacterMapperTest {
    val gameId = Game.ROA2.id

    @Test
    fun `toDomain handles simple character`() {
        // given
        val dto = DtoCharacterSource.kragg
        val expectedId = "kragg"
        val expectedDisplayName = "Kragg"
        val expectedRemoteQueryId = "Kragg"
        val expectedWikiUrl = "https://dragdown.wiki/wiki/RoA2/Kragg"

        // when
        val result = dto.toDomain(emptyMap(), gameId)

        //then
        assertThat(result.id).isEqualTo(expectedId)
        assertThat(result.displayName).isEqualTo(expectedDisplayName)
        assertThat(result.remoteQueryId).isEqualTo(expectedRemoteQueryId)
        assertThat(result.wikiUrl).isEqualTo(expectedWikiUrl)
    }

    @Test
    fun `toDomain handles characters with multi word names`() {
        // given
        val dto = DtoCharacterSource.reina
        val expectedId = "la_reina"
        val expectedDisplayName = "La Reina"
        val expectedRemoteQueryId = "La Reina"
        val expectedWikiUrl = "https://dragdown.wiki/wiki/RoA2/La_Reina"
        val expectedAliases = listOf("reina")

        // when
        val result = dto.toDomain(emptyMap(), gameId)

        //then
        assertThat(result.id).isEqualTo(expectedId)
        assertThat(result.displayName).isEqualTo(expectedDisplayName)
        assertThat(result.remoteQueryId).isEqualTo(expectedRemoteQueryId)
        assertThat(result.wikiUrl).isEqualTo(expectedWikiUrl)
        assertThat(result.aliasList).isEqualTo(expectedAliases)
    }
}

private object DtoCharacterSource {
    val kragg = CharacterResponseDto(
        chara = "Kragg",
        dacusSpeedMultiplier = 1f,
        weight = 108,
        frictionGround = 0.71f,
        frictionAir = 0.11f,
        dashFrames = 12,
        dashSpeed = 16.3f,
        dashAcceleration = 4.5f,
        runSpeedMax = 16.3f,
        runTurnAcceleration = 1.42f,
        runTurnFrames = 21,
        walkAccelerationMax = 0.7f,
        walkSpeedMax = 8.5f,
        gravity = 1.7f,
        hitstunGravity = 1.55f,
        fallSpeedMax = 28.8f,
        fastFallSpeed = 37.2f,
        airAcceleration = 0.75f,
        airSpeedHorizontalMax = 11.33f,
        jumpSpeedHorizontalMax = 12.5f,
        fullHopSpeed = 34f,
        shortHopSpeed = 20.96f,
        doubleJumpSpeed = 34f,
        doubleJumpMaxHorizontalSpeed = 12.5f,
        airDodgeSpeed = 25f,
        airDodgeFriction = 1.4f,
        rollSpeed = 30f,
        shieldSizeMultiplier = null,
        ledgeStandSpeed = 16f,
        ledgeRollSpeed = 32f,
        ledgeJumpMaxHorizontalAirSpeed = 11f,
        getupRollSpeed = 30f,
        techRollSpeed = 31.5f,
        wallJumpSpeedY = 30f,
        wallJumpSpeedX = 14.5f,
    )
    val reina = CharacterResponseDto(
        chara = "La Reina",
        dacusSpeedMultiplier = 1f,
        weight = 101,
        frictionGround = 0.6f,
        frictionAir = 0.16f,
        dashFrames = 13,
        dashSpeed = 15.5f,
        dashAcceleration = 4.8f,
        runSpeedMax = 17.75f,
        runTurnAcceleration = 2.5f,
        runTurnFrames = 19,
        walkAccelerationMax = 0.56f,
        walkSpeedMax = 7.8f,
        gravity = 1.6f,
        hitstunGravity = 1.55f,
        fallSpeedMax = 24.5f,
        fastFallSpeed = 35f,
        airAcceleration = 0.77f,
        airSpeedHorizontalMax = 11.9f,
        jumpSpeedHorizontalMax = 13f,
        fullHopSpeed = 33.3f,
        shortHopSpeed = 22f,
        doubleJumpSpeed = 33f,
        doubleJumpMaxHorizontalSpeed = 12f,
        airDodgeSpeed = 24f,
        airDodgeFriction = 1.4f,
        rollSpeed = 30f,
        shieldSizeMultiplier = null,
        ledgeStandSpeed = 10f,
        ledgeRollSpeed = 28f,
        ledgeJumpMaxHorizontalAirSpeed = 11f,
        getupRollSpeed = 29f,
        techRollSpeed = 31f,
        wallJumpSpeedY = 29f,
        wallJumpSpeedX = 13f,
    )
}
