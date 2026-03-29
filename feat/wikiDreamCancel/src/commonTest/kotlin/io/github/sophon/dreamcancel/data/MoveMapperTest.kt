package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.util.add2dAliases
import io.github.sophon.core.util.decodeHtmlEntities
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.useForwardVariantOnly
import io.github.sophon.core.wiki.domain.model.Character
import kotlin.test.Test

class MoveMapperTest {
    val kofId = Game.KoFXV.id
    val bj = Character(
        id = "B.Jenet",
        displayName = "B. Jenet",
        queryName = "b.jenet",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet",
    )

    @Test
    fun `input and alias test`() {
        // given
        val input = "(close) 4/6C"
        val expectedInput = "c6c"
        val expectedAliases = listOf("c6c")

        // when
        val resultInput = input
            .orDash()
            .decodeHtmlEntities()
            .normalize2dInputs()
            .useForwardVariantOnly()
            .lowercase()
        val resultAliases = resultInput.add2dAliases()

        //then
        assertThat(resultInput).isEqualTo(expectedInput)
        assertThat(resultAliases).isEqualTo(expectedAliases)
    }

    @Test
    fun `form aliases handles multi-button inputs`() {
        // given
        val move = MoveSource.aurora
        val expected = listOf("236236b", "236236d")

        // when
        val result = move.toDomain(
            gameId = kofId,
            character = bj,
            imageUrlMap = emptyMap(),
        )

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
}

private object MoveSource {
    val aurora = MoveDto(
        chara = "B.Jenet",
        moveId = "bjenet_236236k",
        name = "Aurora",
        input = "236236B/D",
        damage = "206 ([20+10*8+40]+70)",
        guard = "Mid",
        startup = "5",
        active = "7 (1) 1 (1) 1 (6) 1 (1) 1 (1) 1 (9) 1 (1) 1 (1) 1",
        recovery = "59 (27 on ground)",
        hitAdv = "HKD (43)",
        blockAdv = "-66",
        invul = "Full Body: 1 to 11 (11 Frames)",
        cancel = "advanced, climax",
        images = "XV_bjenet_236236b_ima.png",
        hitboxes = "XV_bjenet_236236k.png, XV_bjenet_236236k2.png, XV_bjenet_236236k3.png, XV_bjenet_236236k4.png",
        guardDamage = "0",
    )
}