package io.github.sophon.fightingnerd.app.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

internal class MoveFilterTest {
    private val jab = jinMove(input = "1", startup = "i10", onHit = "+8", onBlock = "+1")
    private val oneTwo = jinMove(input = "1,2", startup = "i10", onHit = "+5", onBlock = "-3")
    private val electric = jinMove(
        input = "f,n,d,d/f+2",
        startup = "i11",
        onHit = "+34a (+24)",
        onBlock = "+5",
        filterNameSet = setOf("Homing"),
    )
    private val heatSmash = jinMove(
        input = "H.2+3",
        startup = "i16",
        onBlock = "-9",
        filterNameSet = setOf("Heat", "PowerCrush"),
    )
    private val moveList = listOf(jab, oneTwo, electric, heatSmash)

    @Test
    fun `named filter keeps the moves the wiki matched`() {
        // given
        val filter = MoveFilter.Named("PowerCrush")
        val expected = listOf(heatSmash)

        // when
        val result = moveList.filter { move -> filter.matches(move) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `startup filter keeps the moves within its range`() {
        // given
        val filter = MoveFilter.Startup(from = null, to = 12)
        val expected = listOf(jab, oneTwo, electric)

        // when
        val result = moveList.filter { move -> filter.matches(move) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `on hit filter skips the moves without a frame value`() {
        // given
        val filter = MoveFilter.OnHit(from = 10, to = null)
        val expected = listOf(electric)

        // when
        val result = moveList.filter { move -> filter.matches(move) }

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `on block filter keeps the moves within its range`() {
        // given
        val filter = MoveFilter.OnBlock(from = 0, to = null)
        val expected = listOf(jab, electric)

        // when
        val result = moveList.filter { move -> filter.matches(move) }

        // then
        assertThat(result).isEqualTo(expected)
    }


    private fun jinMove(
        input: String,
        startup: String? = null,
        onHit: String? = null,
        onBlock: String? = null,
        filterNameSet: Set<String> = emptySet(),
    ): Move {
        val move = Move(
            input = input,
            startup = startup,
            onHit = onHit,
            onBlock = onBlock,
            urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-$input"),
            groupId = "n",
            filterNameSet = filterNameSet,
        )
        return move
    }
}
