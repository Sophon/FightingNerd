package io.github.sophon.botdiscord.feat.wikiWavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class GetStancesUseCaseTest {
    private val getMovesUseCase = GetMovesUseCase()
    private val usecase = GetStancesUseCase(getMovesUseCase)
    private val wiki = FakeWikiClient()

    @Test
    fun `use case returns stances of a character`() = runTest {
        // given
        val expected = listOf(
            BotOutput.EmbedButton(label = "1", action = BotOutput.EmbedButton.Action.Query("stance jin zen")),
            BotOutput.EmbedButton(label = "2", action = BotOutput.EmbedButton.Action.Query("stance jin dvs")),
        )

        // when
        val result = usecase.invoke(wiki.featureInfo, wiki, "jin")

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.buttons?.buttonList).isEqualTo(expected)
    }

    @Test
    fun `use case returns moves of a character's stance`() = runTest {
        // given
        val expected = listOf(
            BotOutput.EmbedButton(label = "1", action = BotOutput.EmbedButton.Action.Query("jin zen1")),
            BotOutput.EmbedButton(label = "2", action = BotOutput.EmbedButton.Action.Query("jin zen1+2")),
            BotOutput.EmbedButton(label = "3", action = BotOutput.EmbedButton.Action.Query("jin zen12")),
        )

        // when
        val result = usecase.invoke(wiki.featureInfo, wiki, "jin zen")

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.buttons?.buttonList).isEqualTo(expected)
    }
    
    @Test
    fun `use case returns no stances for a stance-less character`() = runTest {
        // given
        val expected = emptyList<BotOutput.EmbedButton>()

        // when
        val result = usecase.invoke(wiki.featureInfo, wiki, "law")

        //then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as? Result.Success)?.data?.buttons?.buttonList).isEqualTo(expected)
    }

    @Test
    fun `use case returns error for an unknown character`() = runTest {
        // given
        val expected = BotError.UnknownCharacter::class

        // when
        val result = usecase.invoke(wiki.featureInfo, wiki, "unknown")

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(expected)
    }
}
