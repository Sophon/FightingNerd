package io.github.sophon.dreamcancel.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlin.test.Test

class MoveMapperTest {
    val kofId = Game.KoFXV.id
    val bj = Character(
        id = "B.Jenet",
        displayName = "B. Jenet",
        remoteQueryId = "b.jenet",
        wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet",
    )


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

    @Test
    fun `toDomain handles close and OR input`() {
        // given
        val move = MoveSource.byeByeBoo
        val expectedInput = "c4/6c"
        val expectedAlias = listOf(
            "c4c",
            "c6c",
            "c.4c",
            "cl4c",
            "cl.4c",
            "c.6c",
            "cl6c",
            "cl.6c",
        )

        // when
        val result = move.toDomain(kofId, bj, emptyMap())

        //then
        assertThat(result.input).isEqualTo(expectedInput)
        assertThat(result.aliases).isEqualTo(expectedAlias)
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
    val byeByeBoo = MoveDto(
        chara = "B.Jenet",
        moveId = "bjenet_cthrow",
        name = "Bye-Bye Boo",
        idle = "",
        rank = "",
        input = "(close) 4/6C",
        images = "XV_bjenet_cthrow_ima.png",
        hitboxes = "XV_bjenet_cthrow.png",
        damage = "100 (50+50)",
        guard = "N/A",
        cancel = "",
        startup = "1",
        active = "1",
        recovery = "0",
        hitAdv = "HKD (52)",
        blockAdv = "Unblockable",
        invul = "",
        guardDamage = "0",
    )
}
