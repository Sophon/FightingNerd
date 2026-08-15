package io.github.sophon.botdiscord.feat.core.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
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
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GetCharacterUseCaseTest {
    private val useCase = GetCharacterUseCase()

    //region Successful Character and Move Fetch
    @Test
    fun `invoke returns character with fastest normals`() = runTest {
        // given
        val character = createCharacter("Ryu")
        val moves = listOf(
            createMove("5LP", startup = "4"),
            createMove("5MP", startup = "6"),
            createMove("2LP", startup = "4"),
            createMove("236P", startup = "8"), // special move, not a normal
        )
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moves),
        )

        // when
        val result = useCase.invoke(wiki, "Ryu")

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val (char, fastestNormals) = (result as Result.Success).data
        assertThat(char.id).isEqualTo("Ryu")
        assertThat(fastestNormals).hasSize(2)
        assertThat(fastestNormals.map { it.input }).isEqualTo(listOf("5LP", "2LP"))
    }
    //endregion

    //region Character Fetch Failure
    @Test
    fun `invoke returns error when character is not in character list`() = runTest {
        // given
        val wiki = FakeWikiClient(characterList = emptyList())

        // when
        val result = useCase.invoke(wiki, "InvalidChar")

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.UnknownCharacter::class)
    }
    //endregion

    //region Character Matching Behavior
    @Test
    fun `invoke matches character by alias`() = runTest {
        // given
        val character = createCharacter("kunimitsu", aliasList = listOf("kuni"))
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to emptyList()),
        )

        // when
        val result = useCase.invoke(wiki, "kuni")

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val (char, _) = (result as Result.Success).data
        assertThat(char.id).isEqualTo("kunimitsu")
    }

    @Test
    fun `invoke matches character by display name ignoring case and spaces`() = runTest {
        // given
        val character = createCharacter(name = "chunli", displayName = "Chun Li")
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to emptyList()),
        )

        // when
        val result = useCase.invoke(wiki, "CHUNLI")

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        val (char, _) = (result as Result.Success).data
        assertThat(char.id).isEqualTo("chunli")
    }
    //endregion

    //region Multiple Moves With Same Fastest Startup
    @Test
    fun `invoke returns all moves with same fastest startup`() = runTest {
        // given
        val character = createCharacter("Ken")
        val moves = listOf(
            createMove("5LP", startup = "3"),
            createMove("5LK", startup = "3"),
            createMove("2LP", startup = "3"),
            createMove("5MP", startup = "5"),
            createMove("2MK", startup = "6"),
        )
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moves),
        )

        // when
        val result = useCase.invoke(wiki, "Ken")

        // then
        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(3)
        assertThat(fastestNormals.all { it.startup == "3" }).isEqualTo(true)
    }
    //endregion

    //region No Normals Found
    @Test
    fun `invoke returns empty list when no normals found`() = runTest {
        // given
        val character = createCharacter("Ryu")
        val moves = listOf(
            createMove("236P", startup = "8"),  // special move
            createMove("623K", startup = "5"),  // special move
            createMove("j5LP", startup = "4"),  // 4 characters, not a normal
        )
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moves),
        )

        // when
        val result = useCase.invoke(wiki, "Ryu")

        // then
        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).isEmpty()
    }
    //endregion

    //region Filters Only Valid Normals
    @Test
    fun `invoke filters only moves whose second char is not a digit`() = runTest {
        // given
        val character = createCharacter("Chun-Li", displayName = "Chun-Li")
        val moves = listOf(
            createMove("5LP", startup = "4"),    // valid
            createMove("2MK", startup = "5"),    // valid
            createMove("6HP", startup = "6"),    // invalid: starts with 6
            createMove("3MP", startup = "4"),    // invalid: starts with 3
            createMove("236MP", startup = "2"),  // invalid: starts with 2 but second char is digit
        )
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moves),
        )

        // when
        val result = useCase.invoke(wiki, "Chun-Li")

        // then
        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(1)
        assertThat(fastestNormals.first().input).isEqualTo("5LP")
    }
    //endregion

    //region Handles Non-Numeric Startup Values
    @Test
    fun `invoke handles non-numeric startup values gracefully`() = runTest {
        // given
        val character = createCharacter("Guile")
        val moves = listOf(
            createMove("5LP", startup = "4"),
            createMove("5MP", startup = "invalid"),
            createMove("2LP", startup = "5"),
        )
        val wiki = FakeWikiClient(
            characterList = listOf(character),
            moveListByCharacterId = mapOf(character.id to moves),
        )

        // when
        val result = useCase.invoke(wiki, "Guile")

        // then
        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(1)
        assertThat(fastestNormals.first().input).isEqualTo("5LP")
    }
    //endregion

    //region Test Helpers
    private fun createCharacter(
        name: String,
        displayName: String = name,
        aliasList: List<String> = emptyList(),
    ): Character {
        return Character(
            id = name,
            displayName = displayName,
            remoteQueryId = name,
            wikiUrl = "https://wiki.example.com/$name",
            aliasList = aliasList,
        )
    }

    private fun createMove(
        input: String,
        startup: String? = null,
    ): Move {
        return Move(
            characterId = "TestChar",
            id = input,
            input = input,
            startup = startup,
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
            val moves = moveListByCharacterId[characterId.value].orEmpty()
            return flowOf(moves)
        }

        override suspend fun refreshData(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> = throw NotImplementedError("Not used in this use case")
        override suspend fun clearCache(): EmptyResult<WikiError> = throw NotImplementedError("Not used in this use case")
        override fun getFiltersFor(game: Game): Set<Filter> = emptySet()
    }
    //endregion
}
