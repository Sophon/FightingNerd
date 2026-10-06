package io.github.sophon.wiki.app.service

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.wiki.model.COTWGroups
import io.github.sophon.wiki.model.Default
import io.github.sophon.wiki.model.DreamCancelGroups
import io.github.sophon.wiki.model.KofGroups
import io.github.sophon.wiki.model.MBGroups
import io.github.sophon.wiki.model.MizuumiGroups
import io.github.sophon.wiki.model.SFGroups
import io.github.sophon.wiki.model.WavuGroups
import io.github.sophon.wiki.model.wiki.Game
import kotlin.test.Test

internal class GetGroupsServiceTest {
    private val service = GetGroupsService()

    //region tekken
    @Test
    fun `tekken has the generic groups in input order`() {
        // given
        val expected = tekkenGroupList

        // when
        val result = service(Game.Tekken8, extras = emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `tekken stances follow the generic groups`() {
        // given
        val expected = tekkenGroupList + listOf(WavuGroups.Stance("ZEN"), WavuGroups.Stance("DPD"))

        // when
        val result = service(Game.Tekken8, extras = listOf("ZEN", "DPD"))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `stances are only for tekken`() {
        // given
        val expected = listOf(
            SFGroups.Normal,
            SFGroups.Throw,
            SFGroups.Special,
            SFGroups.Drive,
            SFGroups.Super,
            SFGroups.Taunt,
        )

        // when
        val result = service(Game.StreetFighter6, extras = listOf("ZEN"))

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region shared groups
    @Test
    fun `kofxv mixes the shared dream cancel groups with its own`() {
        // given
        val expected = listOf(
            DreamCancelGroups.Normal,
            KofGroups.Rush,
            KofGroups.Throw,
            DreamCancelGroups.Special,
            KofGroups.Climax,
        )

        // when
        val result = service(Game.KoFXV, extras = emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `cotw mixes the shared dream cancel groups with its own`() {
        // given
        val expected = listOf(
            DreamCancelGroups.Normal,
            COTWGroups.Combination,
            COTWGroups.Throw,
            COTWGroups.Rev,
            COTWGroups.FeintDodge,
            DreamCancelGroups.Special,
            COTWGroups.HiddenGear,
        )

        // when
        val result = service(Game.COTW, extras = emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mbtl mixes the shared mizuumi groups with its own`() {
        // given
        val expected = listOf(
            MizuumiGroups.Normal,
            MBGroups.Universal,
            MizuumiGroups.Special,
            MBGroups.Super,
        )

        // when
        val result = service(Game.MBTL, extras = emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `vsav only has the shared mizuumi groups`() {
        // given
        val expected = listOf(
            MizuumiGroups.Normal,
            MizuumiGroups.Special,
        )

        // when
        val result = service(Game.VSAV, extras = emptyList())

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region no groups
    @Test
    fun `mk1 has no groups`() {
        // when
        val result = service(Game.MK1, extras = emptyList())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `xko has no groups`() {
        // when
        val result = service(Game.Xko, extras = emptyList())

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `the catch-all group is never returned - the caller appends it`() {
        // when
        val result = Game.entries.flatMap { service(it, extras = emptyList()) }

        // then
        assertThat(result).doesNotContain(Default)
    }
    //endregion
}


private val tekkenGroupList = listOf(
    WavuGroups.Heat,
    WavuGroups.Neutral,
    WavuGroups.Forward,
    WavuGroups.DownForward,
    WavuGroups.Down,
    WavuGroups.DownBack,
    WavuGroups.Back,
    WavuGroups.Up,
    WavuGroups.UpBack,
    WavuGroups.Motion,
    WavuGroups.Crouch,
    WavuGroups.WS,
)
