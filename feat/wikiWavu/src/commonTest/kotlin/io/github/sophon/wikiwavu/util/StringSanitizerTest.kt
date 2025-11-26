package io.github.sophon.wikiwavu.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class StringSanitizerTest {
    //region cleanMoveInput - Basic Cleaning
    @Test
    fun `cleanMoveInput trims whitespace`() {
        // Given
        val input = "  df2  "

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("df2")
    }

    @Test
    fun `cleanMoveInput converts to lowercase`() {
        // Given
        val input = "DF2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("df2")
    }

    @Test
    fun `cleanMoveInput removes spaces`() {
        // Given
        val input = "d f 2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("df2")
    }

    @Test
    fun `cleanMoveInput removes commas`() {
        // Given
        val input = "1,1,2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("112")
    }

    @Test
    fun `cleanMoveInput removes forward slashes and plus signs`() {
        // Given
        val input = "d/f+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("df2")
    }
    //endregion

    //region cleanMoveInput - Plus Sign Removal
    @Test
    fun `cleanMoveInput removes plus after d`() {
        // Given
        val input = "d+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("d2")
    }

    @Test
    fun `cleanMoveInput removes plus after f`() {
        // Given
        val input = "f+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("f3")
    }

    @Test
    fun `cleanMoveInput removes plus after u`() {
        // Given
        val input = "u+4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("u4")
    }

    @Test
    fun `cleanMoveInput removes plus after b`() {
        // Given
        val input = "b+1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("b1")
    }

    @Test
    fun `cleanMoveInput removes plus after n`() {
        // Given
        val input = "n+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("n2")
    }

    @Test
    fun `cleanMoveInput removes plus after ws`() {
        // Given
        val input = "ws+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ws2")
    }

    @Test
    fun `cleanMoveInput removes plus after fc`() {
        // Given
        val input = "fc+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("fc3")
    }

    @Test
    fun `cleanMoveInput removes plus after cd`() {
        // Given
        val input = "cd+1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("cd1")
    }

    @Test
    fun `cleanMoveInput removes plus after wr`() {
        // Given
        val input = "wr+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("wr3")
    }

    @Test
    fun `cleanMoveInput removes plus after ra`() {
        // Given
        val input = "ra+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ra2")
    }

    @Test
    fun `cleanMoveInput removes plus after ss`() {
        // Given
        val input = "ss+4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ss4")
    }

    @Test
    fun `cleanMoveInput removes plus after asterisk`() {
        // Given
        val input = "*+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("*2")
    }
    //endregion

    //region cleanMoveInput - Dot Notation
    @Test
    fun `cleanMoveInput removes dot after ss`() {
        // Given
        val input = "ss.4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ss4")
    }

    @Test
    fun `cleanMoveInput removes dot after ws`() {
        // Given
        val input = "ws.2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ws2")
    }

    @Test
    fun `cleanMoveInput removes dot after fc`() {
        // Given
        val input = "fc.3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("fc3")
    }

    @Test
    fun `cleanMoveInput preserves dot in stance notation`() {
        // Given
        val input = "IND.u+1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("indu1+2")
    }

    @Test
    fun `cleanMoveInput keeps dot for heat`() {
        //given
        val input = "h.d/f+1"
        val expected = "h.df1"

        //when
        val result = input.cleanMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `cleanMoveInput handles stance dot`() {
        //given
        val input = "BAD.1+2"
        val expected = "bad1+2"

        //when
        val result = input.cleanMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region cleanMoveInput - Special Conversions
    @Test
    fun `cleanMoveInput converts fff to wr`() {
        // Given
        val input = "fff+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("wr2")
    }

    @Test
    fun `cleanMoveInput converts fff without plus to wr`() {
        // Given
        val input = "fff2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("wr2")
    }

    @Test
    fun `cleanMoveInput converts fnddf to cd`() {
        // Given
        val input = "fnddf+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("cd2")
    }

    @Test
    fun `cleanMoveInput only converts fnddf at start`() {
        // Given
        val input = "1fnddf2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("1fnddf2")
    }

    @Test
    fun `cleanMoveInput converts rage dot to r dot`() {
        // Given
        val input = "rage.df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("r.df2")
    }

    @Test
    fun `cleanMoveInput converts heat dot to h dot`() {
        // Given
        val input = "heat.df2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("h.df2")
    }
    //endregion

    //region cleanMoveInput - Complex Combinations
    @Test
    fun `cleanMoveInput handles complex input with multiple notations`() {
        // Given
        val input = "f, f+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ff3")
    }

    @Test
    fun `cleanMoveInput handles crouch dash notation that becomes cd`() {
        // Given
        val input = "f, n, d, d/f+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("cd2")
    }

    @Test
    fun `cleanMoveInput handles multiple button inputs`() {
        // Given
        val input = "1+2+3+4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("1+2+3+4")
    }

    @Test
    fun `cleanMoveInput handles while standing with slash`() {
        // Given
        val input = "WS/2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ws2")
    }

    @Test
    fun `cleanMoveInput handles complex yoshimitsu move`() {
        // Given
        val input = "1SS.1,1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("1ss11")
    }

    @Test
    fun `cleanMoveInput handles running input`() {
        // Given
        val input = "f, f, f+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("wr3")
    }

    @Test
    fun `cleanMoveInput handles heat with complex motion`() {
        // Given
        val input = "Heat.d/f+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("h.df2")
    }

    @Test
    fun `cleanMoveInput handles rage with complex motion`() {
        // Given
        val input = "Rage.d/f+1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("r.df1+2")
    }

    @Test
    fun `cleanMoveInput handles down forward notation`() {
        // Given
        val input = "d/f+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("df2")
    }

    @Test
    fun `cleanMoveInput handles down back notation`() {
        // Given
        val input = "d/b+4"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("db4")
    }

    @Test
    fun `cleanMoveInput handles up forward notation`() {
        // Given
        val input = "u/f+3"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("uf3")
    }

    @Test
    fun `cleanMoveInput handles up back notation`() {
        // Given
        val input = "u/b+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("ub2")
    }
    //endregion

    //region cleanMoveInput - Edge Cases
    @Test
    fun `cleanMoveInput handles empty string`() {
        // Given
        val input = ""

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `cleanMoveInput handles only whitespace`() {
        // Given
        val input = "   "

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("")
    }

    @Test
    fun `cleanMoveInput handles single character`() {
        // Given
        val input = "1"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun `cleanMoveInput handles number only`() {
        // Given
        val input = "123"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("123")
    }

    @Test
    fun `cleanMoveInput preserves plus between numbers`() {
        // Given
        val input = "1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("1+2")
    }

    @Test
    fun `cleanMoveInput handles mixed case input`() {
        // Given
        val input = "InD.u+1+2"

        // When
        val result = input.cleanMoveInput()

        // Then
        assertThat(result).isEqualTo("indu1+2")
    }
    //endregion
}