package io.github.sophon.fightingnerd.adapter.outbound.wiki

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.sophon.fightingnerd.app.model.game.GBVSRCharProperties
import io.github.sophon.fightingnerd.app.model.game.MBTLMoveProperties
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import kotlin.test.Test
import io.github.sophon.wiki.model.CharacterGameProperties as WikiCharacterGameProperties
import io.github.sophon.wiki.model.MoveGameProperties as WikiMoveGameProperties
import io.github.sophon.wiki.model.game.GBVSRCharProperties as WikiGBVSRCharProperties
import io.github.sophon.wiki.model.game.MBTLMoveProperties as WikiMBTLMoveProperties
import io.github.sophon.wiki.model.game.T8Properties as WikiT8Properties

internal class WikiGamePropertiesMappersTest {
    @Test
    fun `tekken move properties keep every flag`() {
        // given
        val wikiProperties = WikiT8Properties(
            isHeat = true,
            isHoming = true,
            stance = "ZEN",
            isPowerCrush = true,
            isHighCrush = true,
            isLowCrush = true,
            hasWallInteraction = true,
            hasFloorInteraction = true,
        )
        val expected = T8Properties(
            isHeat = true,
            isHoming = true,
            stance = "ZEN",
            isPowerCrush = true,
            isHighCrush = true,
            isLowCrush = true,
            hasWallInteraction = true,
            hasFloorInteraction = true,
        )

        // when
        val result = wikiProperties.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `melty move properties keep the mizuumi property`() {
        // given
        val wikiProperties = WikiMBTLMoveProperties(mizuumiProperty = "Clash", cost = "100%")
        val expected = MBTLMoveProperties(mizuumiProperty = "Clash", cost = "100%")

        // when
        val result = wikiProperties.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `granblue character properties keep the nested jump and close range`() {
        // given
        val wikiProperties = WikiGBVSRCharProperties(
            jump = WikiGBVSRCharProperties.Jump(
                pre = "4",
                forwardDistance = "1.7",
                superForwardDistance = "2.4",
                backDistance = "1.5",
                superBackDistance = "2.1",
                gravity = "0.4",
                superGravity = "0.45",
                superHeight = "2.8",
            ),
            backdash = "22 (1-7 invuln)",
            walkSpeed = "3.6",
            walkSpeedBack = "3.2",
            dashInitial = "12",
            dashAcceleration = "0.6",
            closeRange = WikiGBVSRCharProperties.CloseRange(l = "0.7", m = "0.6", h = "0.6"),
        )
        val expected = GBVSRCharProperties(
            jump = GBVSRCharProperties.Jump(
                pre = "4",
                forwardDistance = "1.7",
                superForwardDistance = "2.4",
                backDistance = "1.5",
                superBackDistance = "2.1",
                gravity = "0.4",
                superGravity = "0.45",
                superHeight = "2.8",
            ),
            backdash = "22 (1-7 invuln)",
            walkSpeed = "3.6",
            walkSpeedBack = "3.2",
            dashInitial = "12",
            dashAcceleration = "0.6",
            closeRange = GBVSRCharProperties.CloseRange(l = "0.7", m = "0.6", h = "0.6"),
        )

        // when
        val result = wikiProperties.toDomain()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unknown move properties are dropped`() {
        // given
        val wikiProperties = object : WikiMoveGameProperties {}

        // when
        val result = wikiProperties.toDomain()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `unknown character properties are dropped`() {
        // given
        val wikiProperties = object : WikiCharacterGameProperties {}

        // when
        val result = wikiProperties.toDomain()

        // then
        assertThat(result).isNull()
    }
}
