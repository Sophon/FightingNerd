package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlin.test.Test

class DustLoopNormalizerTest {

    //region input
    @Test
    fun `close input becomes cl`() {
        //given
        val move = DustLoopMoveSource.closeSlash
        val expected = "cls"

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.solBadguy)

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `or input becomes slash alternatives`() {
        //given
        val move = DustLoopMoveSource.airThrow
        val expected = "j6d/j4d"

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.solBadguy)

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `close input gets close aliases`() {
        //given
        val move = DustLoopMoveSource.closeSlash
        val expected = listOf("cl.s", "c.s", "cs")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.solBadguy)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `or input splits into aliases`() {
        //given
        val move = DustLoopMoveSource.airThrow
        val expected = listOf("j6d", "j4d", "j.6d", "j.4d")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.solBadguy)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `button alternatives after a charge expand into aliases`() {
        //given
        val move = DustLoopMoveSource.split
        val expected = listOf("[4]6s~k", "[4]6h~k")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.may)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `release input also matches without brackets`() {
        //given
        val move = DustLoopMoveSource.mistFiner
        val expected = listOf("214p")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.johnny)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki level 1 drops the level`() {
        //given
        val move = DustLoopMoveSource.hLevel1
        val expected = listOf("2h")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.nagoriyuki)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki higher level keeps its number`() {
        //given
        val move = DustLoopMoveSource.sLevel3
        val expected = listOf("2s3")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.nagoriyuki)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki blood rage becomes b`() {
        //given
        val move = DustLoopMoveSource.hBr
        val expected = listOf("2hb")

        //when
        val result = move.normalizeDustLoop(Game.GGST, DustLoopCharacterSource.nagoriyuki)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `GBVSR button alternatives expand into aliases`() {
        //given
        val move = DustLoopMoveSource.silence
        val expected = listOf("22m~l", "22m~m")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR, DustLoopCharacterSource.sandalphon)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Narmaya K stance suffix also becomes a prefix`() {
        //given
        val move = DustLoopMoveSource.fhk
        val expected = listOf("f.h[k]", "k.fh")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR, DustLoopCharacterSource.narmaya)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Narmaya G stance suffix also becomes a prefix`() {
        //given
        val move = DustLoopMoveSource.fhg
        val expected = listOf("f.h[g]", "g.fh")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR, DustLoopCharacterSource.narmaya)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

private object DustLoopCharacterSource {
    val solBadguy = CharacterId("sol_badguy")
    val may = CharacterId("may")
    val johnny = CharacterId("johnny")
    val nagoriyuki = CharacterId("nagoriyuki")
    val sandalphon = CharacterId("sandalphon")
    val narmaya = CharacterId("narmaya")
}

/**
 * Moves as the DustLoop adapter maps them - wiki notation, no aliases yet.
 */
private object DustLoopMoveSource {
    val closeSlash = dustLoopMove(input = "c.S", wikiPage = "GGST/Sol_Badguy")
    val airThrow = dustLoopMove(input = "j.6D or j.4D", name = "Air Throw", wikiPage = "GGST/Sol_Badguy")
    val split = dustLoopMove(input = "[4]6S/H~K", name = "Split", wikiPage = "GGST/May")
    val mistFiner = dustLoopMove(input = "214]P[", name = "Mist Finer (Upward)", wikiPage = "GGST/Johnny")
    val hLevel1 = dustLoopMove(input = "2H Level 1", name = "Level 1", wikiPage = "GGST/Nagoriyuki")
    val sLevel3 = dustLoopMove(input = "2S Level 3", name = "Level 3", wikiPage = "GGST/Nagoriyuki")
    val hBr = dustLoopMove(input = "2H Level BR", name = "Blood Rage", wikiPage = "GGST/Nagoriyuki")
    val silence = dustLoopMove(input = "22M~L/M", name = "22M Silence", wikiPage = "GBVSR/Sandalphon")
    val fhk = dustLoopMove(input = "f.H[k]", wikiPage = "GBVSR/Narmaya")
    val fhg = dustLoopMove(input = "f.H[g]", wikiPage = "GBVSR/Narmaya")
}

private fun dustLoopMove(
    input: String,
    wikiPage: String,
    name: String? = null,
): Move {
    val move = Move(
        name = name,
        input = input,
        urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/$wikiPage"),
    )
    return move
}
