package io.github.sophon.botdiscord.feat.ewgf.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.discord.feat.ewgf.EwgfOperations
import io.github.sophon.discord.feat.ewgf.usecase.ParseQueryIntoOperationUseCase
import kotlin.test.Test
import io.github.sophon.core.architecture.Result

class ParseQueryIntoOperationUseCaseTest {
    private val usecase = ParseQueryIntoOperationUseCase()

    @Test
    fun `usecase recognizes no parameters as data`() {
        // given
        val query = ""
        val expectedResult = Result.Success::class
        val expectedOp = EwgfOperations.Operation.Data

        // when
        val result = usecase.invoke(query)

        //then
        assertThat(result).isInstanceOf(expectedResult)
        assertThat((result as Result.Success).data).isEqualTo(expectedOp)
    }

    @Test
    fun `usecase handles tag`() {
        // given
        val query = "<@786351781168939038>"
        val expectedResult = Result.Success::class
        val expectedOp = EwgfOperations.Operation.Search("786351781168939038")

        // when
        val result = usecase.invoke(query)

        //then
        assertThat(result).isInstanceOf(expectedResult)
        assertThat((result as Result.Success).data).isEqualTo(expectedOp)
    }

    @Test
    fun `usecase detects bad tag`() {
        // given
        val query = "@111111111111"
        val expectedResult = Result.Error::class

        // when
        val result = usecase.invoke(query)

        //then
        assertThat(result).isInstanceOf(expectedResult)
    }
}
