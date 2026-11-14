package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DreamCancelCharacterRemoteMapperTest {

    @Test
    fun `query name joins words by underscore`() {
        //given
        val chara = "B. Jenet"
        val expected = "B._Jenet"

        //when
        val result = chara.createQueryName()

        //then
        assertThat(result).isEqualTo(expected)
    }
}
