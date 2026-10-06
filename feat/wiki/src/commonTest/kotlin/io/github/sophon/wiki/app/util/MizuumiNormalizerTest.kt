package io.github.sophon.wiki.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.app.util.normalizeMizuumi
import io.github.sophon.wiki.model.Move
import kotlin.test.Test

class MizuumiNormalizerTest {

    @Test
    fun `charge notation gets a jump alias and a charge-free alias`() {
        //given
        val move = MizuumiMoveSource.lumenStellaAir
        val expected = move.copy(
            input = "j4_ic_6a",
            aliases = listOf("j[4]6a", "j.4_ic_6a", "j.[4]6a", "j46a"),
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

    @Test
    fun `version keeps the shared wiki notation as an alias`() {
        //given
        val move = MizuumiMoveSource.ak5bClose
        val expected = move.copy(
            input = "5b_close",
            aliases = listOf("5b"),
        )

        //when
        val result = move.normalizeMizuumi()

        //then
        assertThat(result).isEqualTo(expected)
    }
}

/**
 * Moves as the Mizuumi adapter maps them - the input from the move ID, the wiki notation as the only alias.
 */
private object MizuumiMoveSource {
    val lumenStellaAir = mizuumiMove(
        remoteId = "va_j4_ic_6a",
        input = "j4_ic_6a",
        wikiInput = "j[4]6A",
        name = "Lumen Stella (Air)",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Vatista",
    )
    val akJC = mizuumiMove(
        remoteId = "ak_jc",
        input = "jc",
        wikiInput = "j.C",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
    )
    val ak214a = mizuumiMove(
        remoteId = "ak_214a",
        input = "214a",
        wikiInput = "214A",
        wikiUrl = "https://mizuumi.wiki/w/Melty_Blood/MBTL/Akiha_Tohno",
    )
    val ak5bClose = mizuumiMove(
        remoteId = "ak_5b_close",
        input = "5b_close",
        wikiInput = "5B",
        wikiUrl = "https://mizuumi.wiki/w/Under_Night_In-Birth/UNI2/Akatsuki",
    )
}

private fun mizuumiMove(
    remoteId: String,
    input: String,
    wikiInput: String,
    wikiUrl: String,
    name: String? = null,
): Move {
    val move = Move(
        input = input,
        remoteId = remoteId,
        aliases = listOf(wikiInput),
        name = name,
        urls = Move.Urls(wikiUrl = wikiUrl),
    )
    return move
}
