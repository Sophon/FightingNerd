package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.featureConfig.model.Game
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
    fun `id joins words and dots by underscore`() {
        //given
        val chara = "B. Jenet"
        val expected = "b_jenet"

        //when
        val result = chara.toCharacter(Game.KoFXV, iconUrlMap = emptyMap())

        //then
        assertThat(result.id).isEqualTo(expected)
    }
}
