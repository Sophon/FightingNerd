package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

internal class NormalizeMoveInputServiceTest {
    @Test
    fun `tekken string loses its commas`() {
        // given
        val expected = "113"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.Tekken8, "1,1,3")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `tekken string loses its spaces`() {
        // given
        val expected = "112"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.Tekken8, "1 1 2")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `tekken motion loses its plus`() {
        // given
        val expected = "df1"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.Tekken8, "DF+1")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `dustloop close normal gets the close prefix`() {
        // given
        val expected = "cls"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.GGST, "c.S")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mizuumi jump normal loses its dot`() {
        // given
        val expected = "jc"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.MBTL, "j.C")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `supercombo input loses spaces and case`() {
        // given
        val expected = "236hp"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.StreetFighter6, "236 HP")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `xko input keeps its dot`() {
        // given
        val expected = "j.m"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.Xko, "j.M")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `dreamcancel close normal gets the close prefix`() {
        // given
        val expected = "clc"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.KoFXV, "c.C")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `dragdown input is unchanged`() {
        // given
        val expected = "DTilt"
        val service = NormalizeMoveInputService()

        // when
        val result = service(Game.ROA2, "DTilt")

        // then
        assertThat(result).isEqualTo(expected)
    }
}
