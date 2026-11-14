package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

class DragDownCharacterRemoteMapperTest {

    //region id
    @Test
    fun `id is the query name - the service normalizes it`() {
        //given
        val dto = DragDownCharacterDtoSource.reina
        val expected = CharacterId(Game.ROA2, "La Reina")

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.id).isEqualTo(expected)
    }
    //endregion

    //region names
    @Test
    fun `display name keeps the wiki name`() {
        //given
        val dto = DragDownCharacterDtoSource.reina
        val expected = "La Reina"

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.displayName).isEqualTo(expected)
    }

    @Test
    fun `query name keeps the wiki name`() {
        //given
        val dto = DragDownCharacterDtoSource.reina
        val expected = "La Reina"

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.remoteQueryId).isEqualTo(expected)
    }
    //endregion

    //region wiki url
    @Test
    fun `wiki url is the game url and the name`() {
        //given
        val dto = DragDownCharacterDtoSource.kragg
        val expected = "https://dragdown.wiki/wiki/RoA2/Kragg"

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }

    @Test
    fun `multi-word wiki url joins words by underscore`() {
        //given
        val dto = DragDownCharacterDtoSource.reina
        val expected = "https://dragdown.wiki/wiki/RoA2/La_Reina"

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.wikiUrl).isEqualTo(expected)
    }
    //endregion

    @Test
    fun `alias is the last word of the name`() {
        //given
        val dto = DragDownCharacterDtoSource.reina
        val expected = listOf("reina")

        //when
        val result = dto.toCharacter()

        //then
        assertThat(result.aliasList).isEqualTo(expected)
    }
}

private fun DragDownCharacterResponseDto.toCharacter(): Character {
    val character = listOf(this).toDomain(Game.ROA2, imageUrlMap = emptyMap()).single()
    return character
}

private object DragDownCharacterDtoSource {
    val kragg = DragDownCharacterResponseDto(
        chara = "Kragg",
        dacusSpeedMultiplier = 1.0,
        weight = 108,
        frictionGround = 0.71,
        frictionAir = 0.11,
        dashFrames = 12,
        dashSpeed = 16.3,
        dashAcceleration = 4.5,
        runSpeedMax = 16.3,
        runTurnAcceleration = 1.42,
        runTurnFrames = 21,
        walkAccelerationMax = 0.7,
        walkSpeedMax = 8.5,
        gravity = 1.7,
        hitstunGravity = 1.55,
        fallSpeedMax = 28.8,
        fastFallSpeed = 37.2,
        airAcceleration = 0.75,
        airSpeedHorizontalMax = 11.33,
        jumpSpeedHorizontalMax = 12.5,
        fullHopSpeed = 34.0,
        shortHopSpeed = 20.96,
        doubleJumpSpeed = 34.0,
        doubleJumpMaxHorizontalSpeed = 12.5,
        airDodgeSpeed = 25.0,
        airDodgeFriction = 1.4,
        rollSpeed = 30.0,
        shieldSizeMultiplier = null,
        ledgeStandSpeed = 16.0,
        ledgeRollSpeed = 32.0,
        ledgeJumpMaxHorizontalAirSpeed = 11.0,
        getupRollSpeed = 30.0,
        techRollSpeed = 31.5,
        wallJumpSpeedY = 30.0,
        wallJumpSpeedX = 14.5,
    )
    val reina = DragDownCharacterResponseDto(
        chara = "La Reina",
        dacusSpeedMultiplier = 1.0,
        weight = 101,
        frictionGround = 0.6,
        frictionAir = 0.16,
        dashFrames = 13,
        dashSpeed = 15.5,
        dashAcceleration = 4.8,
        runSpeedMax = 17.75,
        runTurnAcceleration = 2.5,
        runTurnFrames = 19,
        walkAccelerationMax = 0.56,
        walkSpeedMax = 7.8,
        gravity = 1.6,
        hitstunGravity = 1.55,
        fallSpeedMax = 24.5,
        fastFallSpeed = 35.0,
        airAcceleration = 0.77,
        airSpeedHorizontalMax = 11.9,
        jumpSpeedHorizontalMax = 13.0,
        fullHopSpeed = 33.3,
        shortHopSpeed = 22.0,
        doubleJumpSpeed = 33.0,
        doubleJumpMaxHorizontalSpeed = 12.0,
        airDodgeSpeed = 24.0,
        airDodgeFriction = 1.4,
        rollSpeed = 30.0,
        shieldSizeMultiplier = null,
        ledgeStandSpeed = 10.0,
        ledgeRollSpeed = 28.0,
        ledgeJumpMaxHorizontalAirSpeed = 11.0,
        getupRollSpeed = 29.0,
        techRollSpeed = 31.0,
        wallJumpSpeedY = 29.0,
        wallJumpSpeedX = 13.0,
    )
}
