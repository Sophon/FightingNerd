package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class WavuNormalizerTest {
    //region Minimize
    @Test
    fun `minimizing strips commas and pluses but keeps the notation`() {
        //given
        val input = "f,f,F+2+4"
        val expected = "fff2+4"

        //when
        val result = input.minimizeTekkenMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `minimizing keeps the stance dot`() {
        //given
        val input = "hFC.1+2"
        val expected = "hfc.1+2"

        //when
        val result = input.minimizeTekkenMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `minimizing keeps the crouch dash motion`() {
        //given
        val input = "H.f,n,d,df+1+2"
        val expected = "h.fnddf1+2"

        //when
        val result = input.minimizeTekkenMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `trims whitespace`() {
        // Given
        val input = "  df2  "
        val expected = "df2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes spaces`() {
        // Given
        val input = "d f 2"
        val expected = "df2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts to lowercase`() {
        // Given
        val input = "DF2"
        val expected = "df2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `held up directions followed by a plus or number keep their case`() {
        // Given
        val inputUb = "UB+4"
        val expectedUb = "UB4"
        val inputU = "U+4"
        val expectedU = "U4"
        val inputUf = "UF+4"
        val inputUfNumber = "UF4"
        val expectedUf = "UF4"

        // When
        val resultUb = inputUb.minimizeTekkenMoveInput()
        val resultU = inputU.minimizeTekkenMoveInput()
        val resultUf = inputUf.minimizeTekkenMoveInput()
        val resultUfNumber = inputUfNumber.minimizeTekkenMoveInput()

        // Then
        assertThat(resultUb).isEqualTo(expectedUb)
        assertThat(resultU).isEqualTo(expectedU)
        assertThat(resultUf).isEqualTo(expectedUf)
        assertThat(resultUfNumber).isEqualTo(expectedUf)
    }

    @Test
    fun `removes commas`() {
        // Given
        val input = "1,1,2"
        val expected = "112"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes forward slashes and plus signs`() {
        // Given
        val input = "d/f+2"
        val expected = "df2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `removes plus after directions and motions`() {
        // Given
        val inputD = "d+2"
        val expectedD = "d2"
        val inputF = "f+3"
        val expectedF = "f3"
        val inputU = "u+4"
        val expectedU = "u4"
        val inputB = "b+1"
        val expectedB = "b1"
        val inputN = "n+2"
        val expectedN = "n2"
        val inputWs = "ws+2"
        val expectedWs = "ws2"
        val inputFc = "fc+3"
        val expectedFc = "fc3"
        val inputCd = "cd+1"
        val expectedCd = "cd1"
        val inputWr = "wr+3"
        val expectedWr = "wr3"
        val inputRa = "ra+2"
        val expectedRa = "ra2"
        val inputFff = "fff+3"
        val expectedFff = "fff3"
        val inputAsterisk = "*+2"
        val expectedAsterisk = "*2"

        // When
        val resultD = inputD.minimizeTekkenMoveInput()
        val resultF = inputF.minimizeTekkenMoveInput()
        val resultU = inputU.minimizeTekkenMoveInput()
        val resultB = inputB.minimizeTekkenMoveInput()
        val resultN = inputN.minimizeTekkenMoveInput()
        val resultWs = inputWs.minimizeTekkenMoveInput()
        val resultFc = inputFc.minimizeTekkenMoveInput()
        val resultCd = inputCd.minimizeTekkenMoveInput()
        val resultWr = inputWr.minimizeTekkenMoveInput()
        val resultRa = inputRa.minimizeTekkenMoveInput()
        val resultFff = inputFff.minimizeTekkenMoveInput()
        val resultAsterisk = inputAsterisk.minimizeTekkenMoveInput()

        // Then
        assertThat(resultD).isEqualTo(expectedD)
        assertThat(resultF).isEqualTo(expectedF)
        assertThat(resultU).isEqualTo(expectedU)
        assertThat(resultB).isEqualTo(expectedB)
        assertThat(resultN).isEqualTo(expectedN)
        assertThat(resultWs).isEqualTo(expectedWs)
        assertThat(resultFc).isEqualTo(expectedFc)
        assertThat(resultCd).isEqualTo(expectedCd)
        assertThat(resultWr).isEqualTo(expectedWr)
        assertThat(resultRa).isEqualTo(expectedRa)
        assertThat(resultFff).isEqualTo(expectedFff)
        assertThat(resultAsterisk).isEqualTo(expectedAsterisk)
    }

    @Test
    fun `plus is kept for buttons`() {
        // Given
        val input = "1+2+3+4"
        val expected = "1+2+3+4"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `only direction plus is removed`() {
        // Given
        val input = "df+1+2"
        val expected = "df1+2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles diagonal direction`() {
        // Given
        val inputDf = "d/f+2"
        val expectedDf = "df2"
        val inputDb = "d/b+4"
        val expectedDb = "db4"
        val inputUf = "u/f+3"
        val expectedUf = "uf3"
        val inputUb = "u/b+2"
        val expectedUb = "ub2"

        // When
        val resultDf = inputDf.minimizeTekkenMoveInput()
        val resultDb = inputDb.minimizeTekkenMoveInput()
        val resultUf = inputUf.minimizeTekkenMoveInput()
        val resultUb = inputUb.minimizeTekkenMoveInput()

        // Then
        assertThat(resultDf).isEqualTo(expectedDf)
        assertThat(resultDb).isEqualTo(expectedDb)
        assertThat(resultUf).isEqualTo(expectedUf)
        assertThat(resultUb).isEqualTo(expectedUb)
    }

    @Test
    fun `handles empty string`() {
        // Given
        val input = ""
        val expected = ""

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles only whitespace`() {
        // Given
        val input = "   "
        val expected = ""

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles neutral button`() {
        // Given
        val input = "1"
        val expected = "1"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles mixed case input`() {
        // Given
        val input = "InD.u+1+2"
        val expected = "ind.u1+2"

        // When
        val result = input.minimizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Normalize
    @Test
    fun `removes dots`() {
        // Given
        val inputSsPlus = "ss+4"
        val expectedSsPlus = "ss4"
        val inputSs = "ss.4"
        val expectedSs = "ss4"
        val inputWs = "ws.2"
        val expectedWs = "ws2"
        val inputFc = "fc.3"
        val expectedFc = "fc3"

        // When
        val resultSsPlus = inputSsPlus.normalizeTekkenMoveInput()
        val resultSs = inputSs.normalizeTekkenMoveInput()
        val resultWs = inputWs.normalizeTekkenMoveInput()
        val resultFc = inputFc.normalizeTekkenMoveInput()

        // Then
        assertThat(resultSsPlus).isEqualTo(expectedSsPlus)
        assertThat(resultSs).isEqualTo(expectedSs)
        assertThat(resultWs).isEqualTo(expectedWs)
        assertThat(resultFc).isEqualTo(expectedFc)
    }

    @Test
    fun `preserves dot in stance notation`() {
        // Given
        val input = "IND.u+1+2"
        val expected = "ind.u1+2"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `keeps dot for heat`() {
        //given
        val input = "h.d/f+1"
        val expected = "h.df1"

        //when
        val result = input.normalizeTekkenMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts fff to wr`() {
        // Given
        val inputPlus = "fff+2"
        val input = "fff2"
        val expected = "wr2"

        // When
        val result = input.normalizeTekkenMoveInput()
        val resultPlus = inputPlus.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
        assertThat(resultPlus).isEqualTo(expected)
    }

    @Test
    fun `converts fnddf to cd`() {
        // Given
        val input = "fnddf+2"
        val expected = "cd.2"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `handles crouch dash notation that becomes cd`() {
        // Given
        val input = "f, n, d, d/f+2"
        val expected = "cd.2"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `converts rage dot to r dot`() {
        // Given
        val input = "rage.df2"
        val expected = "r.df2"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `translates to qcf`() {
        //given
        val string = "d,df,f+2"
        val expected = "qcf2"

        //when
        val result = string.normalizeTekkenMoveInput()

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move string loses its commas`() {
        // Given
        val input = "1,1,3"
        val expected = "113"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move string loses its spaces`() {
        // Given
        val input = "1 1 2"
        val expected = "112"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `motion loses its plus`() {
        // Given
        val input = "DF+1"
        val expected = "df1"

        // When
        val result = input.normalizeTekkenMoveInput()

        // Then
        assertThat(result).isEqualTo(expected)
    }
}
