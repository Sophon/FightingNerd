package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Move
import kotlin.test.Test

class MizuumiNormalizerTest {

    @Test
    fun `charge input gets a jump alias and a charge-free alias`() {
        //given
        val move = MizuumiMoveSource.lumenStellaAir
        val expected = move.copy(
            input = "j[4]6a",
            aliases = listOf("j.[4]6a", "j46a"),
        )

        //when
        val result = move.normalizeMizuumi()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `jump input drops the dot and keeps it as an alias`() {
        //given
        val move = MizuumiMoveSource.akJC
        val expected = move.copy(
            input = "jc",
            aliases = listOf("j.c"),
        )

        //when
        val result = move.normalizeMizuumi()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `special input is lowercased without aliases`() {
        //given
        val move = MizuumiMoveSource.ak214a
        val expected = move.copy(
            input = "214a",
            aliases = emptyList(),
        )

        //when
        val result = move.normalizeMizuumi()

        //then
        assertThat(result).isEqualTo(expected)
    }
}

/**
 * Moves as the Mizuumi adapter maps them - wiki notation, no aliases yet.
 */
private object MizuumiMoveSource {
    val lumenStellaAir = Move(
        characterId = "vatista",
        id = "va_j4_ic_6a",
        name = "Lumen Stella (Air)",
        input = "j[4]6A",
        urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Vatista"),
    )
    val akJC = Move(
        characterId = "akiha_tohno",
        id = "ak_jc",
        input = "j.C",
        urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno"),
    )
    val ak214a = Move(
        characterId = "akiha_tohno",
        id = "ak_214a",
        input = "214A",
        urls = Move.Urls(wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno"),
    )
}
