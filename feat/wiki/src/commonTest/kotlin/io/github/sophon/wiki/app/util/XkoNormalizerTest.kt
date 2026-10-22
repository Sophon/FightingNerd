package io.github.sophon.wiki.app.util

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.app.util.normalizeXko
import io.github.sophon.wiki.model.Move
import kotlin.test.Test

class XkoNormalizerTest {

    @Test
    fun `input is lowercased`() {
        //given
        val move = XkoMoveSource.jinx5M
        val expected = "5m"

        //when
        val result = move.normalizeXko()

        //then
        assertThat(result.input).isEqualTo(expected)
    }

    @Test
    fun `button combination also matches without parentheses`() {
        //given
        val move = XkoMoveSource.jinxJMH
        val expected = "j.m+h"

        //when
        val result = move.normalizeXko()

        //then
        assertThat(result.aliases).contains(expected)
    }
}

/**
 * Moves as the Xko adapter maps them - wiki notation, no aliases yet.
 */
private object XkoMoveSource {
    val jinx5M = jinxMove(input = "5M")
    val jinxJMH = jinxMove(input = "j.(M+H)")
}

private fun jinxMove(input: String): Move {
    val move = Move(
        input = input,
        urls = Move.Urls(wikiUrl = "https://wiki.play2xko.com/en-us/Jinx#$input"),
    )
    return move
}
