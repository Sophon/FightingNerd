package io.github.sophon.botdiscord.feat.wikiWavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStringFollowupsUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetStringFollowupsUseCaseTest {
    private val getMovesUseCase = GetMovesUseCase()
    private val usecase = GetStringFollowupsUseCase(getMovesUseCase)
    private val wiki = FakeWikiClient()

    @Test
    fun `use case returns followups when starter has followups`() = runTest {
        // given
        val expected = listOf(
            BotOutput.EmbedButton(label = "1", action = BotOutput.EmbedButton.Action.Query("law 1")),
            BotOutput.EmbedButton(label = "2", action = BotOutput.EmbedButton.Action.Query("law 12")),
            BotOutput.EmbedButton(label = "3", action = BotOutput.EmbedButton.Action.Query("law 112")),
        )

        // when
        val result = usecase.invoke(wiki, "law 1", wiki.featureInfo)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.buttons?.buttonList).isEqualTo(expected)
    }

    @Test
    fun `use case returns only the starter when it has no followups`() = runTest {
        // given
        val expected = listOf(
            BotOutput.EmbedButton(label = "1", action = BotOutput.EmbedButton.Action.Query("jin df4")),
        )

        // when
        val result = usecase.invoke(wiki, "jin df4", wiki.featureInfo)

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.buttons?.buttonList).isEqualTo(expected)
    }

    @Test
    fun `use case returns error when character doesn't exist`() = runTest {
        // given
        val expected = BotError.UnknownCharacter::class

        // when
        val result = usecase.invoke(wiki, "unknown df4", wiki.featureInfo)

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(expected)
    }
}