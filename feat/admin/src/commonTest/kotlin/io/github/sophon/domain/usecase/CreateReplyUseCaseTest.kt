package io.github.sophon.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import kotlin.test.Test

class CreateReplyUseCaseTest {
    private val usecase = CreateReplyUseCase()

    @Test
    fun `usecase handles normal input`() {
        //given
        val string = "phd_cunnilingus-126041495038853120-717398042562658347 some feedback"
        val expected = Result.Success(
            AdminResult(
                Source(
                    username = "phd_cunnilingus",
                    id = "126041495038853120",
                    channelId = "717398042562658347",
                ),
                message = "some feedback",
            )
        )

        //when
        val result = usecase.invoke(string)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `usecase handles missing source`() {
        //given
        val string = "phd_cunnilingus-126041495038853120- some feedback"

        //when
        val result = usecase.invoke(string)
        val error = (result as Result.Error).error

        //then
        assertThat(result).isInstanceOf(Result.Error::class)

        assertThat(error).isInstanceOf(AdminError.WrongReplyFormat::class)
        val wrongFormat = error as AdminError.WrongReplyFormat

        assertThat(wrongFormat.query).isEqualTo(string)
    }

    @Test
    fun `usecase handles missing message`() {
        //given
        val string = "phd_cunnilingus-126041495038853120-717398042562658347"
        val expected = Result.Error(
            AdminError.WrongReplyFormat(
                "phd_cunnilingus-126041495038853120-717398042562658347"
            )
        )

        //when
        val result = usecase.invoke(string)
        val error = (result as Result.Error).error

        //then
        assertThat(result).isInstanceOf(Result.Error::class)

        assertThat(error).isInstanceOf(AdminError.WrongReplyFormat::class)
        val wrongFormat = error as AdminError.WrongReplyFormat

        assertThat(wrongFormat.query).isEqualTo(string)
    }
}