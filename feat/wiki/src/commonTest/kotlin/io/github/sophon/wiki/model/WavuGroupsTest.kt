package io.github.sophon.wiki.model

import assertk.assertThat
import assertk.assertions.isTrue
import kotlin.test.Test

class WavuGroupsTest {
    //region up
    @Test
    fun `up takes every tapped up direction`() {
        // given
        val moveList = listOf(move("u4"), move("ub1"), move("uf3"))

        // when
        val result = moveList.all(WavuGroups.Up.predicate)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `up takes every held up direction`() {
        // given
        val moveList = listOf(move("U4"), move("UB1"), move("UF4"))

        // when
        val result = moveList.all(WavuGroups.Up.predicate)

        // then
        assertThat(result).isTrue()
    }
    //endregion
}


private fun move(input: String): Move {
    val move = Move(
        input = input,
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/King_movelist#King-$input"),
    )
    return move
}
