package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.wiki.model.Move
import kotlin.test.Test

class DreamCancelNormalizerTest {

    @Test
    fun `button alternatives expand into aliases`() {
        //given
        val move = DreamCancelMoveSource.aurora
        val expected = listOf("236236b", "236236d")

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `close input becomes c`() {
        //given
        val move = DreamCancelMoveSource.byeByeBoo
        val expected = "c4/6c"

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `close direction alternatives expand into close aliases`() {
        //given
        val move = DreamCancelMoveSource.byeByeBoo
        val expected = listOf(
            "c4c",
            "c6c",
            "c.4c",
            "cl4c",
            "cl.4c",
            "c.6c",
            "cl6c",
            "cl.6c",
        )

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
}

/**
 * Moves as the DreamCancel adapter maps them - wiki notation, no aliases yet.
 */
private object DreamCancelMoveSource {
    val aurora = bJenetMove(id = "bjenet_236236k", name = "Aurora", input = "236236B/D")
    val byeByeBoo = bJenetMove(id = "bjenet_cthrow", name = "Bye-Bye Boo", input = "(close) 4/6C")
}

private fun bJenetMove(
    id: String,
    name: String,
    input: String,
): Move {
    val move = Move(
        characterId = "b_jenet",
        id = id,
        name = name,
        input = input,
        urls = Move.Urls(wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV/B.Jenet"),
    )
    return move
}
