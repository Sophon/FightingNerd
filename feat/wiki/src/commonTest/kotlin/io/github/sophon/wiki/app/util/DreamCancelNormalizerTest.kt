package io.github.sophon.wiki.app.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.app.util.normalizeDreamCancel
import io.github.sophon.wiki.model.Move
import kotlin.test.Test

class DreamCancelNormalizerTest {

    @Test
    fun `button alternatives expand into aliases`() {
        //given
        val move = DreamCancelMoveSource.aurora
        val expected = listOf("236236b/d", "236236b", "236236d")

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `close direction alternatives expand into close aliases`() {
        //given
        val move = DreamCancelMoveSource.byeByeBoo
        val expected = listOf(
            "cl4/6c",
            "cl4c",
            "cl6c",
            "cl.4c",
            "c.4c",
            "c4c",
            "cl.6c",
            "c.6c",
            "c6c",
        )

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }

    @Test
    fun `close D and Blowback get different inputs`() {
        //given
        val moveList = listOf(DreamCancelMoveSource.closeD, DreamCancelMoveSource.blowback)
        val expected = listOf("cld", "cd")

        //when
        val result = moveList.map { move -> move.normalizeDreamCancel() }

        //then
        assertThat(result.map { it.input }).isEqualTo(expected)
    }

    @Test
    fun `close input gets the other close spellings as aliases`() {
        //given
        val move = DreamCancelMoveSource.closeD
        val expected = listOf("cl.d", "c.d", "cd")

        //when
        val result = move.normalizeDreamCancel()

        //then
        assertThat(result.aliases).isEqualTo(expected)
    }
}

/**
 * Moves as the DreamCancel adapter maps them - the input from the move ID, the wiki notation as the only alias.
 */
private object DreamCancelMoveSource {
    val aurora = dreamCancelMove(remoteId = "bjenet_236236k", input = "236236k", wikiInput = "236236B/D", name = "Aurora")
    val byeByeBoo = dreamCancelMove(remoteId = "bjenet_cthrow", input = "cthrow", wikiInput = "(close) 4/6C", name = "Bye-Bye Boo")
    val closeD = dreamCancelMove(remoteId = "kyo_cld", input = "cld", wikiInput = "c.D", name = "close D")
    val blowback = dreamCancelMove(remoteId = "kyo_cd", input = "cd", wikiInput = "CD", name = "Blowback")
}

private fun dreamCancelMove(
    remoteId: String,
    input: String,
    wikiInput: String,
    name: String,
): Move {
    val move = Move(
        input = input,
        remoteId = remoteId,
        aliases = listOf(wikiInput),
        name = name,
        urls = Move.Urls(wikiUrl = "https://dreamcancel.com/wiki/The_King_of_Fighters_XV"),
    )
    return move
}
