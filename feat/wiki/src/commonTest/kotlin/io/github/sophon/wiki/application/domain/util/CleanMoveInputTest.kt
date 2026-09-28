package io.github.sophon.wiki.application.domain.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class CleanMoveInputTest {
    //region Basic Cleaning
    @Test
    fun `trims whitespace`() {
        // Given
        val input = "  df2  "
        val expected = "df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts to lowercase`() {
        // Given
        val input = "DF2"
        val expected = "df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes spaces`() {
        // Given
        val input = "d f 2"
        val expected = "df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes commas`() {
        // Given
        val input = "1,1,2"
        val expected = "112"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes forward slashes and plus signs`() {
        // Given
        val input = "d/f+2"
        val expected = "df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Plus Sign Removal
    @Test
    fun `removes plus after d`() {
        // Given
        val input = "d+2"
        val expected = "d2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after f`() {
        // Given
        val input = "f+3"
        val expected = "f3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after u`() {
        // Given
        val input = "u+4"
        val expected = "u4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after b`() {
        // Given
        val input = "b+1"
        val expected = "b1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after n`() {
        // Given
        val input = "n+2"
        val expected = "n2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after ws`() {
        // Given
        val input = "ws+2"
        val expected = "ws2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after fc`() {
        // Given
        val input = "fc+3"
        val expected = "fc3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after cd`() {
        // Given
        val input = "cd+1"
        val expected = "cd1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after wr`() {
        // Given
        val input = "wr+3"
        val expected = "wr3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after ra`() {
        // Given
        val input = "ra+2"
        val expected = "ra2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after ss`() {
        // Given
        val input = "ss+4"
        val expected = "ss.4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after asterisk`() {
        // Given
        val input = "*+2"
        val expected = "*2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Dot Notation

    @Test
    fun `removes dot after ws`() {
        // Given
        val input = "ws.2"
        val expected = "ws2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes dot after fc`() {
        // Given
        val input = "fc.3"
        val expected = "fc3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `preserves dot in stance notation`() {
        // Given
        val input = "IND.u+1+2"
        val expected = "ind.u1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `keeps dot for heat`() {
        //given
        val input = "h.d/f+1"
        val expected = "h.df1"

        //when
        val result = input.cleanMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Special Conversions
    @Test
    fun `converts fff to wr`() {
        // Given
        val input = "fff+2"
        val expected = "wr2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts fff without plus to wr`() {
        // Given
        val input = "fff2"
        val expected = "wr2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts fnddf to cd`() {
        // Given
        val input = "fnddf+2"
        val expected = "cd.2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `only converts fnddf at start`() {
        // Given
        val input = "1fnddf2"
        val expected = "1cd.2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts rage dot to r dot`() {
        // Given
        val input = "rage.df2"
        val expected = "r.df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts heat dot to h dot`() {
        // Given
        val input = "heat.df2"
        val expected = "h.df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Complex Combinations
    @Test
    fun `handles complex input with multiple notations`() {
        // Given
        val input = "f, f+3"
        val expected = "ff3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles crouch dash notation that becomes cd`() {
        // Given
        val input = "f, n, d, d/f+2"
        val expected = "cd.2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles multiple button inputs`() {
        // Given
        val input = "1+2+3+4"
        val expected = "1+2+3+4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles while standing with slash`() {
        // Given
        val input = "WS/2"
        val expected = "ws2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles running input`() {
        // Given
        val input = "f, f, f+3"
        val expected = "wr3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles heat with complex motion`() {
        // Given
        val input = "Heat.d/f+2"
        val expected = "h.df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles rage with complex motion`() {
        // Given
        val input = "Rage.d/f+1+2"
        val expected = "r.df1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles down forward notation`() {
        // Given
        val input = "d/f+2"
        val expected = "df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles down back notation`() {
        // Given
        val input = "d/b+4"
        val expected = "db4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles up forward notation`() {
        // Given
        val input = "u/f+3"
        val expected = "uf3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles up back notation`() {
        // Given
        val input = "u/b+2"
        val expected = "ub2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Edge Cases
    @Test
    fun `handles empty string`() {
        // Given
        val input = ""
        val expected = ""

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles only whitespace`() {
        // Given
        val input = "   "
        val expected = ""

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles single character`() {
        // Given
        val input = "1"
        val expected = "1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles number only`() {
        // Given
        val input = "123"
        val expected = "123"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `preserves plus between numbers`() {
        // Given
        val input = "1+2"
        val expected = "1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles mixed case input`() {
        // Given
        val input = "InD.u+1+2"
        val expected = "ind.u1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region motion
    @Test
    fun `translates to qcf`() {
        //given
        val string = "d,df,f+2"
        val expected = "qcf2"

        //when
        val result = string.cleanMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion
}