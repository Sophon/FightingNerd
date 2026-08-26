package io.github.sophon.dreamcancel.data.remote

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CharacterRemoteMapperTest {
    //region query name
    @Test
    fun `createQueryName handles names with spaces`() {
        val characterName = "B. Jenet"
        val expected = "B._Jenet"

        //when
        val result = characterName.createQueryName()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}