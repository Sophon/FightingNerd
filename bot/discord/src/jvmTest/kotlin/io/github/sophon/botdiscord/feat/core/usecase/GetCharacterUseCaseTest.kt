package io.github.sophon.botdiscord.feat.core.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.feat.core.usecase.GetCharacterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.core.wiki.model.Character

class GetCharacterUseCaseTest {
    //region Successful Character and Move Fetch
    @Test
    fun `invoke returns character with fastest normals`() = runTest {
        val character = createCharacter("Ryu")
        val moves = listOf(
            createMove("5LP", startup = "4"),
            createMove("5MP", startup = "6"),
            createMove("2LP", startup = "4"),
            createMove("236P", startup = "8") // special move, not a normal
        )
        val wikiClient = FakeWikiClient(character, moves)
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Ryu")

        assertThat(result).isInstanceOf(Result.Success::class)
        val (char, fastestNormals) = (result as Result.Success).data
        assertThat(char.id).isEqualTo("Ryu")
        assertThat(fastestNormals).hasSize(2)
        assertThat(fastestNormals.map { it.input }).isEqualTo(listOf("5LP", "2LP"))
    }
    //endregion

    //region Character Fetch Failure
    @Test
    fun `invoke returns error when character fetch fails`() = runTest {
        val wikiClient = FakeWikiClient(
            characterResult = Result.Error(WikiError.UnknownCharacter("Character not found"))
        )
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "InvalidChar")

        assertThat(result).isInstanceOf(Result.Error::class)
    }
    //endregion

    //region Move List Fetch Failure
    @Test
    fun `invoke returns error when move list fetch fails`() = runTest {
        val character = createCharacter("Ryu")
        val wikiClient = FakeWikiClient(
            character,
            moveListResult = Result.Error(WikiError.UnknownMove("Moves not found"))
        )
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Ryu")

        assertThat(result).isInstanceOf(Result.Error::class)
    }
    //endregion

    //region Multiple Moves With Same Fastest Startup
    @Test
    fun `invoke returns all moves with same fastest startup`() = runTest {
        val character = createCharacter("Ken")
        val moves = listOf(
            createMove("5LP", startup = "3"),
            createMove("5LK", startup = "3"),
            createMove("2LP", startup = "3"),
            createMove("5MP", startup = "5"),
            createMove("2MK", startup = "6")
        )
        val wikiClient = FakeWikiClient(character, moves)
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Ken")

        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(3)
        assertThat(fastestNormals.all { it.startup == "3" }).isEqualTo(true)
    }
    //endregion

    //region No Normals Found
    @Test
    fun `invoke returns empty list when no normals found`() = runTest {
        val character = createCharacter("Ryu")
        val moves = listOf(
            createMove("236P", startup = "8"),  // special move
            createMove("623K", startup = "5"),  // special move
            createMove("j5LP", startup = "4")   // 4 characters, not a normal
        )
        val wikiClient = FakeWikiClient(character, moves)
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Ryu")

        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).isEmpty()
    }
    //endregion

    //region Filters Only Valid Normals
    @Test
    fun `invoke filters only moves with length 3 starting with 5 or 2`() = runTest {
        val character = createCharacter("Chun-Li")
        val moves = listOf(
            createMove("5LP", startup = "4"),   // valid
            createMove("2MK", startup = "5"),   // valid
            createMove("6HP", startup = "6"),   // invalid: starts with 6
            createMove("3MP", startup = "4"),   // invalid: starts with 3
            createMove("236MP", startup = "2")    // invalid: starts with 2 but no button
        )
        val wikiClient = FakeWikiClient(character, moves)
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Chun-Li")

        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(1)
        assertThat(fastestNormals.first().input).isEqualTo("5LP")
    }
    //endregion

    //region Handles Non-Numeric Startup Values
    @Test
    fun `invoke handles non-numeric startup values gracefully`() = runTest {
        val character = createCharacter("Guile")
        val moves = listOf(
            createMove("5LP", startup = "4"),
            createMove("5MP", startup = "invalid"),
            createMove("2LP", startup = "5")
        )
        val wikiClient = FakeWikiClient(character, moves)
        val useCase = GetCharacterUseCase()

        val result = useCase.invoke(wikiClient, "Guile")

        val (_, fastestNormals) = (result as Result.Success).data
        assertThat(fastestNormals).hasSize(1)
        assertThat(fastestNormals.first().input).isEqualTo("5LP")
    }
    //endregion

    //region Test Helpers
    private fun createCharacter(name: String): Character {
        return Character(
            id = name,
            displayName = name,
            remoteQueryId = name,
            wikiUrl = "https://wiki.example.com/$name",
            aliasList = emptyList()
        )
    }

    private fun createMove(
        input: String,
        startup: String? = null
    ): Move {
        return Move(
            charName = "TestChar",
            id = input,
            input = input,
            startup = startup,
            urls = Move.Urls(
                wikiUrl = "TODO"
            ),
        )
    }
    //endregion
}

private class FakeWikiClient(
    private val character: Character? = null,
    private val moves: List<Move> = emptyList(),
    private val characterResult: Result<Character, WikiError>? = null,
    private val moveListResult: Result<List<Move>, WikiError>? = null
) : WikiClient {

    override suspend fun fetchCharacter(charName: String): Result<Character, WikiError> {
        return characterResult ?: character?.let { Result.Success(it) }
        ?: Result.Error(WikiError.UnknownCharacter("Character not found"))
    }

    override suspend fun fetchMoveList(charName: String, filter: Filter): Result<List<Move>, WikiError> {
        return moveListResult ?: Result.Success(moves)
    }

    // Stub implementations for other WikiClient methods
    override fun getFeatureInfo() = error("Not implemented")
    override suspend fun downloadCharacterList() = error("Not implemented")
    override suspend fun cacheCharacterList(characterList: List<Character>) = error("Not implemented")
    override suspend fun fetchCharacterList() = error("Not implemented")
    override suspend fun downloadMoveList(characterData: DownloadMoveListUseCase.CharacterData) = error("Not implemented")
    override suspend fun cacheMoveList(character: Character, moveList: List<Move>) = error("Not implemented")
    override suspend fun fetchMove(charName: String, moveQuery: String) = error("Not implemented")
    override suspend fun getLastUpdateTimeStamp() = error("Not implemented")
    override suspend fun clearCache() = error("Not implemented")
}