package io.github.sophon.botdiscord.feat.core.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GetMovesUseCaseTest {
    private val useCase = GetMovesUseCase()

    //region Success Scenarios
    @Test
    fun `useCase returns moves for character deduplicated by input`() = runTest {
        // given
        val charName = "jin"
        val character = createCharacter(charName)
        val moves = listOf(
            createMove(input = "4"),
            createMove(input = "zen2"),
            createMove(input = "zen2"), // duplicate — dropped by distinctBy
            createMove(input = "zen3"),
        )
        val expected = Result.Success(
            character to listOf(
                createMove(input = "4"),
                createMove(input = "zen2"),
                createMove(input = "zen3"),
            )
        )

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moves),
            ),
            characterId = charName,
            filter = Filter.None,
        )

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `useCase applies filter predicate before deduplication`() = runTest {
        // given
        val charName = "jin"
        val character = createCharacter(charName)
        val homingMove = createMove(input = "4", properties = Move.T8Properties(isHoming = true))
        val nonHomingMove = createMove(input = "1", properties = Move.T8Properties(isHoming = false))
        val homingOnly = object : Filter {
            override val name: String = "homing"
            override val predicate: (Move) -> Boolean = { it.t8Properties?.isHoming == true }
        }

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to listOf(homingMove, nonHomingMove)),
            ),
            characterId = charName,
            filter = homingOnly,
        )

        // then
        val (_, moves) = (result as Result.Success).data
        assertThat(moves).isEqualTo(listOf(homingMove))
    }
    //endregion

    //region Failure Scenarios
    @Test
    fun `useCase returns UnknownCharacter when characterId not in list`() = runTest {
        // given
        val wiki = FakeWikiClient(characterList = emptyList())

        // when
        val result = useCase.invoke(
            wiki = wiki,
            characterId = "missing",
            filter = Filter.None,
        )

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.UnknownCharacter::class)
    }
    //endregion

    //region Fakes and helpers
    private fun createCharacter(id: String): Character {
        return Character(
            id = id,
            displayName = id,
            remoteQueryId = id,
            wikiUrl = "",
        )
    }

    private fun createMove(
        input: String,
        properties: Move.T8Properties = Move.T8Properties(isHoming = true),
    ): Move {
        return Move(
            characterId = "Test",
            id = input,
            startup = "",
            input = input,
            urls = Move.Urls(wikiUrl = "TODO"),
            t8Properties = properties,
        )
    }

    private class FakeWikiClient(
        private val characterList: List<Character> = emptyList(),
        private val moveListByCharacterId: Map<String, List<Move>> = emptyMap(),
    ) : WikiClient {
        override val featureInfo: FeatureInfo = WavuFeatureInfo.featureInfo

        override fun subscribeToCharacterList(): Flow<List<Character>> = flowOf(characterList)

        override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
            val moves = moveListByCharacterId[characterId.value].orEmpty()
            return flowOf(moves)
        }

        override suspend fun refreshData(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchCharacter(characterQuery: String): Result<Character, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun downloadMoveListFor(character: Character): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun checkHasCachedMoves(characterId: String): Result<Boolean, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchMoveList(characterQuery: String, filter: Filter): Result<List<Move>, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun fetchMove(characterId: String, moveQuery: String): Result<Move, WikiError> = throw NotImplementedError("Not used in this use case")
    }
    //endregion
}
