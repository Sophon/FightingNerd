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
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.T8Properties
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
        val result = useCase(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moves),
            ),
            characterQuery = charName,
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
        val homingMove = createMove(input = "4", properties = T8Properties(isHoming = true))
        val nonHomingMove = createMove(input = "1", properties = T8Properties(isHoming = false))
        val homingOnly = object : Filter {
            override val name: String = "homing"
            override val predicate: (Move) -> Boolean = { it.t8Properties?.isHoming == true }
        }

        // when
        val result = useCase(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to listOf(homingMove, nonHomingMove)),
            ),
            characterQuery = charName,
            filter = homingOnly,
        )

        // then
        val (_, moves) = (result as Result.Success).data
        assertThat(moves).isEqualTo(listOf(homingMove))
    }

    @Test
    fun `useCase returns moves when query matches character alias`() = runTest {
        // given
        val charName = "jin"
        val alias = "jim"
        val character = createCharacter(charName, aliasList = listOf(alias))
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
        val result = useCase(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moves),
            ),
            characterQuery = alias,
            filter = Filter.None,
        )

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region Failure Scenarios
    @Test
    fun `useCase returns UnknownCharacter when characterId not in list`() = runTest {
        // given
        val wiki = FakeWikiClient(characterList = emptyList())

        // when
        val result = useCase(
            wiki = wiki,
            characterQuery = "missing",
            filter = Filter.None,
        )

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.UnknownCharacter::class)
    }
    //endregion

    //region Fakes and helpers
    private fun createCharacter(
        id: String,
        aliasList: List<String> = listOf(),
    ): Character {
        return Character(
            id = id,
            displayName = id,
            remoteQueryId = id,
            wikiUrl = "",
            aliasList = aliasList,
        )
    }

    private fun createMove(
        input: String,
        properties: T8Properties = T8Properties(isHoming = true),
    ): Move {
        return Move(
            characterId = "Test",
            id = input,
            startup = "",
            input = input,
            urls = Move.Urls(wikiUrl = "TODO"),
            gameProperties = properties,
        )
    }

    private val Move.t8Properties: T8Properties?
        get() = gameProperties as? T8Properties

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

        override fun refreshData(): Flow<RefreshEvent> = throw NotImplementedError("Not used in this use case")
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}
