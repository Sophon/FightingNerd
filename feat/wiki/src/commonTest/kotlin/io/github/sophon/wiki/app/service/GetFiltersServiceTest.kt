package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.BBFilters
import io.github.sophon.wiki.model.GGFilters
import io.github.sophon.wiki.model.MBFilters
import io.github.sophon.wiki.model.UniFilters
import io.github.sophon.wiki.model.VSAVFilters
import io.github.sophon.wiki.model.WavuFilters
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

internal class GetFiltersServiceTest {
    private val service = GetFiltersService()

    @Test
    fun `tekken has every binary wavu filter`() {
        // given
        val expected = setOf(
            WavuFilters.PowerCrush,
            WavuFilters.Heat,
            WavuFilters.Homing,
            WavuFilters.Throw,
            WavuFilters.Stance,
            WavuFilters.LowCrush,
            WavuFilters.HighCrush,
        )

        // when
        val result = service(Game.Tekken8)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `bbcf has its own invincible filter`() {
        // given
        val expected = setOf(BBFilters.Invincible)

        // when
        val result = service(Game.BBCF)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `ggst has its own invincible filter`() {
        // given
        val expected = setOf(GGFilters.Invincible)

        // when
        val result = service(Game.GGST)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mbtl has its own invincible filter`() {
        // given
        val expected = setOf(MBFilters.Invincible)

        // when
        val result = service(Game.MBTL)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `uni2 has its own invincible filter`() {
        // given
        val expected = setOf(UniFilters.Invincible)

        // when
        val result = service(Game.Uni2)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `vsav has its own invincible filter`() {
        // given
        val expected = setOf(VSAVFilters.Invincible)

        // when
        val result = service(Game.VSAV)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `games without game-specific filters have none`() {
        // given
        val gameList = listOf(
            Game.StreetFighter6,
            Game.MK1,
            Game.AVL,
            Game.Xko,
            Game.KoFXV,
            Game.COTW,
            Game.DBFZ,
            Game.GBVSR,
            Game.MTFS,
            Game.ROA2,
        )

        // when
        val result = gameList.flatMap { service(it) }

        // then
        assertThat(result).isEmpty()
    }
}
