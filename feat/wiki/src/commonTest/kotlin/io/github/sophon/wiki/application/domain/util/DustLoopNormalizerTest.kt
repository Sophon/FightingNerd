package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Move
import kotlin.test.Test

class DustLoopNormalizerTest {

    @Test
    fun `id is the character id and the normalized input`() {
        //given
        val move = DustLoopMoveSource.closeSlash
        val expected = "sol_badguy_cs"

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    //region input
    @Test
    fun `close input drops the dot`() {
        //given
        val move = DustLoopMoveSource.closeSlash
        val expected = "cs"

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `or input becomes slash alternatives`() {
        //given
        val move = DustLoopMoveSource.airThrow
        val expected = "j6d/j4d"

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.input).isEqualTo(expected)
    }
    //endregion

    //region aliases
    @Test
    fun `close input gets close aliases`() {
        //given
        val move = DustLoopMoveSource.closeSlash
        val expected = listOf("c.s", "cls", "cl.s")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `or input splits into aliases`() {
        //given
        val move = DustLoopMoveSource.airThrow
        val expected = listOf("j6d", "j4d", "j.6d", "j.4d")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `button alternatives after a charge expand into aliases`() {
        //given
        val move = DustLoopMoveSource.split
        val expected = listOf("[4]6s~k", "[4]6h~k")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `release input also matches without brackets`() {
        //given
        val move = DustLoopMoveSource.mistFiner
        val expected = listOf("214p")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki level 1 drops the level`() {
        //given
        val move = DustLoopMoveSource.hLevel1
        val expected = listOf("2h")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki higher level keeps its number`() {
        //given
        val move = DustLoopMoveSource.sLevel3
        val expected = listOf("2s3")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Nagoriyuki blood rage becomes b`() {
        //given
        val move = DustLoopMoveSource.hBr
        val expected = listOf("2hb")

        //when
        val result = move.normalizeDustLoop(Game.GGST)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `GBVSR button alternatives expand into aliases`() {
        //given
        val move = DustLoopMoveSource.silence
        val expected = listOf("22m~l", "22m~m")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Narmaya K stance suffix also becomes a prefix`() {
        //given
        val move = DustLoopMoveSource.fhk
        val expected = listOf("f.h[k]", "k.fh")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `Narmaya G stance suffix also becomes a prefix`() {
        //given
        val move = DustLoopMoveSource.fhg
        val expected = listOf("f.h[g]", "g.fh")

        //when
        val result = move.normalizeDustLoop(Game.GBVSR)

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
    //endregion
}

/**
 * Moves as the DustLoop adapter maps them - wiki notation, no aliases yet.
 */
private object DustLoopMoveSource {
    val closeSlash = dustLoopMove(characterId = "sol_badguy", input = "c.S", wikiPage = "GGST/Sol_Badguy")
    val airThrow = dustLoopMove(characterId = "sol_badguy", input = "j.6D or j.4D", name = "Air Throw", wikiPage = "GGST/Sol_Badguy")
    val split = dustLoopMove(characterId = "may", input = "[4]6S/H~K", name = "Split", wikiPage = "GGST/May")
    val mistFiner = dustLoopMove(characterId = "johnny", input = "214]P[", name = "Mist Finer (Upward)", wikiPage = "GGST/Johnny")
    val hLevel1 = dustLoopMove(characterId = "nagoriyuki", input = "2H Level 1", name = "Level 1", wikiPage = "GGST/Nagoriyuki")
    val sLevel3 = dustLoopMove(characterId = "nagoriyuki", input = "2S Level 3", name = "Level 3", wikiPage = "GGST/Nagoriyuki")
    val hBr = dustLoopMove(characterId = "nagoriyuki", input = "2H Level BR", name = "Blood Rage", wikiPage = "GGST/Nagoriyuki")
    val silence = dustLoopMove(characterId = "sandalphon", input = "22M~L/M", name = "22M Silence", wikiPage = "GBVSR/Sandalphon")
    val fhk = dustLoopMove(characterId = "narmaya", input = "f.H[k]", wikiPage = "GBVSR/Narmaya")
    val fhg = dustLoopMove(characterId = "narmaya", input = "f.H[g]", wikiPage = "GBVSR/Narmaya")
}

private fun dustLoopMove(
    characterId: String,
    input: String,
    wikiPage: String,
    name: String? = null,
): Move {
    val move = Move(
        characterId = characterId,
        id = "${characterId}_$input",
        name = name,
        input = input,
        urls = Move.Urls(wikiUrl = "https://www.dustloop.com/w/$wikiPage"),
    )
    return move
}
