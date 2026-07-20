package io.github.sophon.botdiscord.feat.wikiWavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.feat.wikiWavu.usecase.GetStancesUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.time.ExperimentalTime

internal class GetStancesUseCaseTest {
    private val getMovesUseCase = GetMovesUseCase()
    private val usecase = GetStancesUseCase(getMovesUseCase)
    private val wiki = FakeClient()

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


    private class FakeClient: WikiClient {
        override val featureInfo: FeatureInfo = WavuFeatureInfo.featureInfo

        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> {
            return when (characterQuery) {
                JIN.id -> Result.Success(JIN)
                LAW.id -> Result.Success(LAW)
                else -> Result.Error(WikiError.UnknownCharacter(characterQuery))
            }
        }

        override suspend fun fetchMoveList(
            characterQuery: String,
            filter: Filter,
        ): Result<List<Move>, WikiError> {
            val moveList = when (characterQuery) {
                JIN.id -> createJinZenMoves() + createJinDvsMoves()
                LAW.id -> createLawMoves()
                else -> return Result.Error(WikiError.UnknownCharacter(characterQuery))
            }
            return Result.Success(moveList.filter(filter.predicate))
        }

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = Result.Success(emptyList())
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = Result.Success(Unit)
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = Result.Success(emptyList())
        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = Result.Success(emptyList())
        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = Result.Success(true)
        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = Result.Success(Unit)
        override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = Result.Error(WikiError.UnknownMove(moveQuery))
        @OptIn(ExperimentalTime::class)
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = Result.Success(null)
        override suspend fun clearCache(): EmptyResult<WikiError> = Result.Success(Unit)
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()

        private fun createJinZenMoves(): List<Move> {
            return listOf(
                createMove(characterId = JIN.id, id = "jin-zen1", input = "zen1", stance = "zen"),
                createMove(characterId = JIN.id, id = "jin-zen1+2", input = "zen1+2", stance = "zen"),
                createMove(characterId = JIN.id, id = "jin-zen12", input = "zen12", stance = "zen"),
            )
        }

        private fun createJinDvsMoves(): List<Move> {
            return listOf(
                createMove(characterId = JIN.id, id = "jin-df4", input = "df4", stance = "dvs"),
            )
        }

        private fun createLawMoves(): List<Move> {
            return listOf(
                createMove(characterId = LAW.id, id = "law-1", input = "1"),
                createMove(characterId = LAW.id, id = "law-2", input = "2"),
            )
        }

        private fun createMove(
            characterId: String,
            id: String,
            input: String,
            stance: String? = null,
        ): Move {
            return Move(
                characterId = characterId,
                id = id,
                input = input,
                urls = Move.Urls(wikiUrl = ""),
                t8Properties = stance?.let { Move.T8Properties(stance = it) },
            )
        }

        private companion object {
            private val JIN = Character(
                id = "jin",
                displayName = "Jin",
                remoteQueryId = "jin",
                wikiUrl = "",
            )
            private val LAW = Character(
                id = "law",
                displayName = "Law",
                remoteQueryId = "law",
                wikiUrl = "",
            )
        }
    }
}
