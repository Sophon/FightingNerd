package io.github.sophon.fightingnerd.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.fightingnerd.app.model.AppError
import kotlin.test.Test

internal class ResultUtilsTest {
    @Test
    fun `wiki error becomes an app error with the wiki error name and inputs`() {
        // given
        val wikiResult: Result<String, WikiError> = Result.Error(WikiError.UnknownMove("Kazuya", "f,n,d,df+3"))
        val expected = Result.Error(AppError.WikiError("UnknownMove(Kazuya, f,n,d,df+3)"))

        // when
        val result = wikiResult.mapWikiError()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `success is kept`() {
        // given
        val wikiResult: Result<String, WikiError> = Result.Success("Kazuya")
        val expected = Result.Success("Kazuya")

        // when
        val result = wikiResult.mapWikiError()

        // then
        assertThat(result).isEqualTo(expected)
    }
}
