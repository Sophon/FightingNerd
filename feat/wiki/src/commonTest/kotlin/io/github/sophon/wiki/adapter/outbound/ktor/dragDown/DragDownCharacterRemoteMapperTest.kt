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
    val reina = DragDownCharacterResponseDto(
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
