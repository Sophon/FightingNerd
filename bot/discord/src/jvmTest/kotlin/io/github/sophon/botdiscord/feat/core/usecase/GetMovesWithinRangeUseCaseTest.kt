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
import io.github.sophon.core.wiki.model.CoreFilters
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.RefreshEvent
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.usecase.GetMovesWithinRangeUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GetMovesWithinRangeUseCaseTest {
    private val useCase = GetMovesWithinRangeUseCase()

    //region Success Scenarios
    @Test
    fun `useCase filters by startup for Startup command`() = runTest {
        // given
        val character = createCharacter("jin")
        val inRangeLow = createMove(input = "1", startup = "10")
        val inRangeHigh = createMove(input = "df1", startup = "13")
        val outOfRange = createMove(input = "wr2", startup = "25")
        val moveList = listOf(inRangeLow, outOfRange, inRangeHigh)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.Startup,
            query = "jin 10 15",
        )

        // then
        val range = (result as Result.Success).data
        val expectedMoveList = listOf(inRangeLow, inRangeHigh)
        assertThat(range.moveList).isEqualTo(expectedMoveList)
        assertThat(range.rangeType).isInstanceOf(CoreFilters.Startup::class)
        assertThat(range.from).isEqualTo(10)
        assertThat(range.to).isEqualTo(15)
        assertThat(range.character).isEqualTo(character)
    }

    @Test
    fun `useCase filters by onBlock for OnBlock command`() = runTest {
        // given
        val character = createCharacter("jin")
        val punishable = createMove(input = "df2", onBlock = "-13")
        val safe = createMove(input = "1", onBlock = "-5")
        val moveList = listOf(punishable, safe)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.OnBlock,
            query = "jin -20 -10",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(punishable))
        assertThat(range.rangeType).isInstanceOf(CoreFilters.OnBlock::class)
    }

    @Test
    fun `useCase filters by onHit for OnHit command`() = runTest {
        // given
        val character = createCharacter("jin")
        val launcher = createMove(input = "ewgf", onHit = "20")
        val nonLauncher = createMove(input = "1", onHit = "5")
        val moveList = listOf(launcher, nonLauncher)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.OnHit,
            query = "jin 15 30",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(launcher))
        assertThat(range.rangeType).isInstanceOf(CoreFilters.OnHit::class)
    }

    @Test
    fun `useCase filters by onCH for OnCounter command`() = runTest {
        // given — OnCounter reuses the Startup filter by design
        val character = createCharacter("jin")
        val fastMove = createMove(input = "1", onCH = "10")
        val slowMove = createMove(input = "wr2", onCH = "25")
        val moveList = listOf(fastMove, slowMove)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.OnCounter,
            query = "jin 10 15",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(fastMove))
        assertThat(range.rangeType).isInstanceOf(CoreFilters.OnCounter::class)
    }

    @Test
    fun `useCase treats inf token as MAX_VALUE upper bound`() = runTest {
        // given
        val character = createCharacter("jin")
        val fastMove = createMove(input = "1", startup = "10")
        val slowMove = createMove(input = "wr2", startup = "25")
        val moveList = listOf(fastMove, slowMove)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.Startup,
            query = "jin 15 inf",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(slowMove))
        assertThat(range.to).isEqualTo(Int.MAX_VALUE)
        assertThat(range.from).isEqualTo(15)
    }

    @Test
    fun `useCase treats -inf token as MIN_VALUE lower bound`() = runTest {
        // given
        val character = createCharacter("jin")
        val punishable = createMove(input = "df2", onBlock = "-13")
        val safe = createMove(input = "1", onBlock = "-5")
        val moveList = listOf(punishable, safe)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.OnBlock,
            query = "jin -inf -10",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(punishable))
        assertThat(range.from).isEqualTo(Int.MIN_VALUE)
        assertThat(range.to).isEqualTo(-10)
    }

    @Test
    fun `useCase sorts range so from is the lower bound`() = runTest {
        // given
        val character = createCharacter("jin")
        val inRange = createMove(input = "1", startup = "12")
        val moveList = listOf(inRange)

        // when — user typed the bounds in reverse order
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.Startup,
            query = "jin 20 10",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.from).isEqualTo(10)
        assertThat(range.to).isEqualTo(20)
        assertThat(range.moveList).isEqualTo(listOf(inRange))
    }

    @Test
    fun `useCase deduplicates moves by input`() = runTest {
        // given
        val character = createCharacter("jin")
        val first = createMove(input = "1", startup = "10")
        val duplicate = createMove(input = "1", startup = "12")
        val other = createMove(input = "df1", startup = "13")
        val moveList = listOf(first, duplicate, other)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.Startup,
            query = "jin 10 15",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.moveList).isEqualTo(listOf(first, other))
    }

    @Test
    fun `useCase matches character by alias`() = runTest {
        // given
        val character = createCharacter("jin", aliasList = listOf("jim"))
        val move = createMove(input = "1", startup = "10")

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to listOf(move)),
            ),
            command = Command.Startup,
            query = "jim 10 15",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.character).isEqualTo(character)
        assertThat(range.moveList).isEqualTo(listOf(move))
    }
    //endregion

    //region Failure Scenarios
    @Test
    fun `useCase returns InvalidQuery when range is missing`() = runTest {
        // given — no space, so rangeQuery is empty
        val character = createCharacter("jin")

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(characterList = listOf(character)),
            command = Command.Startup,
            query = "jin",
        )

        // then
        assertThat((result as Result.Error).error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `useCase treats a single number as a single-point range`() = runTest {
        // given
        val character = createCharacter("jin")
        val onValue = createMove(input = "1", startup = "10")
        val below = createMove(input = "df1", startup = "9")
        val above = createMove(input = "wr2", startup = "11")
        val moveList = listOf(below, onValue, above)

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(
                characterList = listOf(character),
                moveListByCharacterId = mapOf(character.id to moveList),
            ),
            command = Command.Startup,
            query = "jin 10",
        )

        // then
        val range = (result as Result.Success).data
        assertThat(range.from).isEqualTo(10)
        assertThat(range.to).isEqualTo(10)
        assertThat(range.moveList).isEqualTo(listOf(onValue))
    }

    @Test
    fun `useCase returns InvalidQuery when no valid numbers are provided`() = runTest {
        // given
        val character = createCharacter("jin")

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(characterList = listOf(character)),
            command = Command.Startup,
            query = "jin abc",
        )

        // then
        assertThat((result as Result.Error).error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `useCase returns InvalidQuery for unsupported command`() = runTest {
        // given
        val character = createCharacter("jin")

        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(characterList = listOf(character)),
            command = Command.Fd,
            query = "jin 10 15",
        )

        // then
        assertThat((result as Result.Error).error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `useCase returns UnknownCharacter when character is not in list`() = runTest {
        // when
        val result = useCase.invoke(
            wiki = FakeWikiClient(characterList = emptyList()),
            command = Command.Startup,
            query = "missing 10 15",
        )

        // then
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
        startup: String? = null,
        onBlock: String? = null,
        onHit: String? = null,
        onCH: String? = null,
    ): Move {
        return Move(
            characterId = "Test",
            id = input,
            startup = startup,
            onBlock = onBlock,
            onHit = onHit,
            onCH = onCH,
            input = input,
            urls = Move.Urls(wikiUrl = "TODO"),
        )
    }

    private class FakeWikiClient(
        private val characterList: List<Character> = emptyList(),
        private val moveListByCharacterId: Map<String, List<Move>> = emptyMap(),
    ) : WikiClient {
        override val featureInfo: FeatureInfo = WavuFeatureInfo.featureInfo

        override fun subscribeToCharacterList(): Flow<List<Character>> = flowOf(characterList)

        override fun subscribeToMoveList(characterId: CharacterId): Flow<List<Move>> {
            val moveList = moveListByCharacterId[characterId.value].orEmpty()
            return flowOf(moveList)
        }

        override fun refreshData(): Flow<RefreshEvent> = throw NotImplementedError("Not used in this use case")
        override fun subscribeToLastUpdateTimestamp(): Flow<Instant?> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}
