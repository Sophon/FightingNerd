package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.CharacterId
import kotlin.test.Test

class DreamCancelCharacterRemoteMapperTest {

    @Test
    fun `query name joins words by underscore`() {
        //given
        val chara = "B. Jenet"
        val expected = "B._Jenet"

        //when
        val result = chara.toCharacter(Game.KoFXV, iconUrlMap = emptyMap())

        //then
        assertThat(result.remoteQueryId).isEqualTo(expected)
    }

    @Test
    fun `id is the query name - the service normalizes it`() {
        //given
        val chara = "B. Jenet"
        val expected = CharacterId("B._Jenet")

        //when
        val result = chara.toCharacter(Game.KoFXV, iconUrlMap = emptyMap())

        //then
        assertThat(result.id).isEqualTo(expected)
    }

    @Test
    fun `grouping key joins words and dots by underscore`() {
        //given
        val chara = "B. Jenet"
        val expected = "b_jenet"

        //when
        val result = chara.formGroupingKey()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `spellings differing in apostrophes and case share a grouping key`() {
        //given
        val charaList = listOf("Ryo Sakazaki", "ryo sakazaki", "Ryo Saka'zaki")
        val expected = listOf("ryo_sakazaki", "ryo_sakazaki", "ryo_sakazaki")

        //when
        val result = charaList.map { chara -> chara.formGroupingKey() }

        //then
        assertThat(result).isEqualTo(expected)
    }
}
