package io.github.sophon.fightingnerd.app.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

internal class MoveTest {
    private val jab = jinMove(input = "1", name = "Jab")
    private val oneTwo = jinMove(input = "1,2")
    private val electric = jinMove(
        input = "f,n,d,d/f+2",
        name = "Electric Wind Hook Fist",
        aliases = listOf("EWHF"),
    )
    private val heatSmash = jinMove(input = "H.2+3", name = "Heat Smash")
    private val moveList = listOf(jab, oneTwo, electric, heatSmash)

    @Test
    fun `no query matches every move`() {
        // given
        val expected = moveList

        // when
        val result = moveList.filter { move -> move.matches(null) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `query matches the input`() {
        // given
        val expected = listOf(jab, oneTwo)

        // when
        val result = moveList.filter { move -> move.matches("1") }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `query matches the name ignoring case`() {
        // given
        val expected = listOf(heatSmash)

        // when
        val result = moveList.filter { move -> move.matches("heat sm") }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `query matches an alias`() {
        // given
        val expected = listOf(electric)

        // when
        val result = moveList.filter { move -> move.matches("ewhf") }

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun jinMove(
        input: String,
        name: String? = null,
        aliases: List<String> = emptyList(),
    ): Move {
        val move = Move(
            input = input,
            name = name,
            aliases = aliases,
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-$input"),
            groupId = "n",
        )
        return move
    }
}
