@file:OptIn(ExperimentalTime::class)

package io.github.sophon.botdiscord.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.discord.BotError
import io.github.sophon.discord.usecase.SyncWikiDataUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.ExperimentalTime


class SyncWikiDataUseCaseTest {
    //region Test Setup
    private val useCase = SyncWikiDataUseCase()

    private fun createTestCharacter(
        id: String = "jin",
        displayName: String = "Jin Kazama",
        queryName: String = "jin"
    ) = Character(
        id = id,
        displayName = displayName,
        queryName = queryName,
        wikiUrl = "https://wavu.wiki/t/Jin",
        aliasList = listOf("jin", "kazama"),
        images = null,
        sf6Properties = null
    )

    private fun createTestMove(
        charName: String = "jin",
        id: String = "1",
        input: String = "1"
    ) = Move(
        charName = charName,
        id = id,
        input = input,
        damage = "10",
        startup = "i10",
        guard = "h",
        onBlock = "-1",
        onHit = "+5",
        onCH = null,
        name = "Jab",
        recovery = null,
        notes = emptyList(),
        aliases = emptyList(),
        urls = Move.Urls(wikiUrl = "TODO"),
        t8Properties = Move.T8Properties(
            isHeat = false,
            isHoming = false,
            stance = null,
            isPowerCrush = false,
            isHighCrush = false,
            isLowCrush = false
        ),
        sf6Properties = null
    )

    private class FakeWikiClient(
        private val featureInfo: FeatureInfo = FeatureInfo(
            name = "wavu",
            url = "",
            version = "",
            supportedGameSet = setOf(),
            iconUrl = "",
        )
    ) : WikiClient {
        var downloadCharacterListResult: Result<List<Character>, WikiError>? = null
        var cacheCharacterListResult: EmptyResult<WikiError>? = null
        var downloadMoveListResults: Map<String, Result<List<Move>, WikiError>> = emptyMap()
        var cacheMoveListResults: Map<String, EmptyResult<WikiError>> = emptyMap()
        var clearCacheResult: EmptyResult<WikiError>? = null

        val downloadCharacterListCallCount get() = _downloadCharacterListCallCount
        val cacheCharacterListCalls get() = _cacheCharacterListCalls
        val downloadMoveListCalls get() = _downloadMoveListCalls
        val cacheMoveListCalls get() = _cacheMoveListCalls
        val clearCacheCalls get() = _clearCacheCalls

        private var _downloadCharacterListCallCount = 0
        private val _cacheCharacterListCalls = mutableListOf<List<Character>>()
        private val _downloadMoveListCalls = mutableListOf<String>()
        private val _cacheMoveListCalls = mutableListOf<Pair<Character, List<Move>>>()
        private var _clearCacheCalls = 0

        override fun getFeatureInfo(): FeatureInfo = featureInfo

        override suspend fun downloadCharacterList(): Result<List<Character>, WikiError> {
            _downloadCharacterListCallCount++
            return downloadCharacterListResult
                ?: Result.Success(emptyList())
        }

        override suspend fun cacheCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
            _cacheCharacterListCalls.add(characterList)
            return cacheCharacterListResult
                ?: Result.Success(Unit)
        }

        override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
            throw NotImplementedError("Not used in this use case")
        }

        override suspend fun fetchCharacter(charName: String): Result<Character, WikiError> {
            throw NotImplementedError("Not used in this use case")
        }

        override suspend fun downloadMoveList(
            characterData: DownloadMoveListUseCase.CharacterData,
        ): Result<List<Move>, WikiError> {
            _downloadMoveListCalls.add(characterData.name)
            return downloadMoveListResults[characterData.name]
                ?: Result.Success(emptyList())
        }

        override suspend fun cacheMoveList(character: Character, moveList: List<Move>): EmptyResult<WikiError> {
            _cacheMoveListCalls.add(character to moveList)
            return cacheMoveListResults[character.queryName]
                ?: Result.Success(Unit)
        }

        override suspend fun fetchMoveList(charName: String): Result<List<Move>, WikiError> {
            throw NotImplementedError("Not used in this use case")
        }

        override suspend fun fetchMove(charName: String, moveQuery: String): Result<Move, WikiError> {
            throw NotImplementedError("Not used in this use case")
        }

        override suspend fun getLastUpdateTimeStamp(): Result<Instant?, WikiError> {
            throw NotImplementedError("Not used in this use case")
        }

        override suspend fun clearCache(): EmptyResult<WikiError> {
            _clearCacheCalls++
            return clearCacheResult
                ?: Result.Success(Unit)
        }
    }
    //endregion

    //region Success Scenarios
    @Test
    fun `invoke - single wiki with single character and moves succeeds`() = runTest {
        // given
        val character = createTestCharacter()
        val moves = listOf(
            createTestMove(charName = "jin", id = "1", input = "1"),
            createTestMove(charName = "jin", id = "2", input = "2")
        )
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character))
            downloadMoveListResults = mapOf("jin" to Result.Success(moves))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.downloadCharacterListCallCount).isEqualTo(1)
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
        assertThat(wiki.cacheCharacterListCalls).hasSize(1)
        assertThat(wiki.cacheCharacterListCalls.first()).containsExactly(character)
        assertThat(wiki.cacheMoveListCalls).hasSize(1)
        assertThat(wiki.cacheMoveListCalls.first()).isEqualTo(character to moves)
    }

    @Test
    fun `invoke - single wiki with multiple characters and moves succeeds`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", displayName = "Jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", displayName = "Kazuya", queryName = "kazuya")
        val jinMoves = listOf(createTestMove(charName = "jin", id = "1"))
        val kazuyaMoves = listOf(createTestMove(charName = "kazuya", id = "1"))
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya))
            downloadMoveListResults = mapOf(
                "jin" to Result.Success(jinMoves),
                "kazuya" to Result.Success(kazuyaMoves)
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.cacheCharacterListCalls.first()).containsExactly(jin, kazuya)
        assertThat(wiki.cacheMoveListCalls).hasSize(2)
        assertThat(wiki.downloadMoveListCalls).containsExactly("jin", "kazuya")
    }

    @Test
    fun `invoke - multiple wikis all succeed`() = runTest {
        // given
        val character1 = createTestCharacter(id = "jin")
        val character2 = createTestCharacter(id = "ryu")
        val wiki1 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character1))
            downloadMoveListResults = mapOf("jin" to Result.Success(listOf(createTestMove())))
        }
        val wiki2 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character2))
            downloadMoveListResults = mapOf("ryu" to Result.Success(listOf(createTestMove(charName = "ryu"))))
        }

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki1.clearCacheCalls).isEqualTo(1)
        assertThat(wiki2.clearCacheCalls).isEqualTo(1)
    }

    @Test
    fun `invoke - empty character list succeeds`() = runTest {
        // given
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(emptyList())
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf((Result.Success::class))
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
        assertThat(wiki.cacheCharacterListCalls.first()).isEmpty()
        assertThat(wiki.downloadMoveListCalls).isEmpty()
        assertThat(wiki.cacheMoveListCalls).isEmpty()
    }

    @Test
    fun `invoke - character with empty move list succeeds`() = runTest {
        // given
        val character = createTestCharacter()
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character))
            downloadMoveListResults = mapOf("jin" to Result.Success(emptyList()))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.cacheMoveListCalls).isEmpty()
    }
    //endregion

    //region Download Character List Failures
    @Test
    fun `invoke - downloadCharacterList fails does not clear cache`() = runTest {
        // given
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Error(WikiError.DownloadError("Network error"))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.Unknown::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(0)
        assertThat(wiki.cacheCharacterListCalls).isEmpty()
    }
    //endregion

    //region Download Move List Failures
    @Test
    fun `invoke - downloadMoveList fails for one character returns first error`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", queryName = "kazuya")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya))
            downloadMoveListResults = mapOf(
                "jin" to Result.Error(WikiError.DownloadError("Failed to download jin moves")),
                "kazuya" to Result.Success(listOf(createTestMove(charName = "kazuya")))
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(0)
        assertThat(wiki.cacheCharacterListCalls).isEmpty()
    }

    @Test
    fun `invoke - downloadMoveList fails for all characters returns error`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", queryName = "kazuya")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya))
            downloadMoveListResults = mapOf(
                "jin" to Result.Error(WikiError.DownloadError("Failed")),
                "kazuya" to Result.Error(WikiError.DownloadError("Failed"))
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(0)
    }

    @Test
    fun `invoke - downloadMoveList partial success still fails overall`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", queryName = "kazuya")
        val paul = createTestCharacter(id = "paul", queryName = "paul")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya, paul))
            downloadMoveListResults = mapOf(
                "jin" to Result.Success(listOf(createTestMove(charName = "jin"))),
                "kazuya" to Result.Error(WikiError.DownloadError("Failed")),
                "paul" to Result.Success(listOf(createTestMove(charName = "paul")))
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(0)
        assertThat(wiki.downloadMoveListCalls).hasSize(3)
    }
    //endregion

    //region Clear Cache Failures
    @Test
    fun `invoke - clearCache fails stops execution and returns error`() = runTest {
        // given
        val character = createTestCharacter()
        val moves = listOf(createTestMove())
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character))
            downloadMoveListResults = mapOf("jin" to Result.Success(moves))
            clearCacheResult = Result.Error(WikiError.DatabaseError("Failed to clear cache"))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
        assertThat(wiki.cacheCharacterListCalls).isEmpty()
        assertThat(wiki.cacheMoveListCalls).isEmpty()
    }
    //endregion

    //region Cache Character List Failures
    @Test
    fun `invoke - cacheCharacterList fails after clearCache returns error`() = runTest {
        // given
        val character = createTestCharacter()
        val moves = listOf(createTestMove())
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character))
            downloadMoveListResults = mapOf("jin" to Result.Success(moves))
            cacheCharacterListResult = Result.Error(WikiError.DatabaseError("Failed to cache characters"))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
        assertThat(wiki.cacheCharacterListCalls).hasSize(1)
        assertThat(wiki.cacheMoveListCalls).isEmpty()
    }
    //endregion

    //region Cache Move List Failures
    @Test
    fun `invoke - cacheMoveList fails for one character returns error`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", queryName = "kazuya")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya))
            downloadMoveListResults = mapOf(
                "jin" to Result.Success(listOf(createTestMove(charName = "jin"))),
                "kazuya" to Result.Success(listOf(createTestMove(charName = "kazuya")))
            )
            cacheMoveListResults = mapOf(
                "jin" to Result.Error(WikiError.DatabaseError("Failed to cache jin moves"))
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
        assertThat(wiki.cacheCharacterListCalls).hasSize(1)
    }

    @Test
    fun `invoke - cacheMoveList fails for all characters returns first error`() = runTest {
        // given
        val jin = createTestCharacter(id = "jin", queryName = "jin")
        val kazuya = createTestCharacter(id = "kazuya", queryName = "kazuya")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(jin, kazuya))
            downloadMoveListResults = mapOf(
                "jin" to Result.Success(listOf(createTestMove(charName = "jin"))),
                "kazuya" to Result.Success(listOf(createTestMove(charName = "kazuya")))
            )
            cacheMoveListResults = mapOf(
                "jin" to Result.Error(WikiError.DatabaseError("Failed")),
                "kazuya" to Result.Error(WikiError.DatabaseError("Failed"))
            )
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(wiki.clearCacheCalls).isEqualTo(1)
    }
    //endregion

    //region Multiple Wiki Scenarios
    @Test
    fun `invoke - first wiki succeeds second wiki fails returns error`() = runTest {
        // given
        val wiki1 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(createTestCharacter()))
            downloadMoveListResults = mapOf("jin" to Result.Success(listOf(createTestMove())))
        }
        val wiki2 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Error(WikiError.DownloadError("Failed"))
        }

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.Unknown::class)
        assertThat(wiki1.clearCacheCalls).isEqualTo(1)
        assertThat(wiki2.clearCacheCalls).isEqualTo(0)
    }

    @Test
    fun `invoke - all wikis fail returns error with all failures`() = runTest {
        // given
        val wiki1 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Error(WikiError.DownloadError("Wiki1 failed"))
        }
        val wiki2 = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Error(WikiError.DownloadError("Wiki2 failed"))
        }
        val wiki3 = FakeWikiClient().apply {
            clearCacheResult = Result.Error(WikiError.DatabaseError("Wiki3 failed"))
            downloadCharacterListResult = Result.Success(listOf(createTestCharacter()))
            downloadMoveListResults = mapOf("jin" to Result.Success(listOf(createTestMove())))
        }

        // when
        val result = useCase.invoke(listOf(wiki1, wiki2, wiki3))

        // then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat((result as Result.Error).error).isInstanceOf(BotError.Unknown::class)
    }

    @Test
    fun `invoke - empty wiki list returns success`() = runTest {
        // given
        val emptyList = emptyList<WikiClient>()

        // when
        val result = useCase.invoke(emptyList)

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
    }
    //endregion

    //region Edge Cases
    @Test
    fun `invoke - character with empty queryName uses empty string for download`() = runTest {
        // given
        val character = createTestCharacter(queryName = "")
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character))
            downloadMoveListResults = mapOf("" to Result.Success(listOf(createTestMove(charName = ""))))
        }

        // when
        val result = useCase.invoke(listOf(wiki))

        // then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(wiki.downloadMoveListCalls).containsExactly("")
    }

    @Test
    fun `invoke - operations are performed in correct order`() = runTest {
        // given
        val character = createTestCharacter()
        val moves = listOf(createTestMove())
        val operationOrder = mutableListOf<String>()
        val wiki = FakeWikiClient().apply {
            downloadCharacterListResult = Result.Success(listOf(character)).also {
                operationOrder.add("downloadCharacterList")
            }
            downloadMoveListResults = mapOf("jin" to Result.Success(moves).also {
                operationOrder.add("downloadMoveList")
            })
            clearCacheResult = Result.Success(Unit).also {
                operationOrder.add("clearCache")
            }
            cacheCharacterListResult = Result.Success(Unit).also {
                operationOrder.add("cacheCharacterList")
            }
            cacheMoveListResults = mapOf("jin" to Result.Success(Unit).also {
                operationOrder.add("cacheMoveList")
            })
        }

        // when
        useCase.invoke(listOf(wiki))

        // then
        assertThat(operationOrder).containsExactly(
            "downloadCharacterList",
            "downloadMoveList",
            "clearCache",
            "cacheCharacterList",
            "cacheMoveList"
        )
    }

    @Test
    fun `invoke - different WikiError types are all converted to BotError`() = runTest {
        // given
        val testCases = listOf(
            WikiError.DownloadError("download"),
            WikiError.DatabaseError("database"),
            WikiError.UnknownCharacter("unknown"),
            WikiError.UnknownMove("move")
        )

        testCases.forEach { wikiError ->
            val wiki = FakeWikiClient().apply {
                downloadCharacterListResult = Result.Error(wikiError)
            }

            // when
            val result = useCase.invoke(listOf(wiki))

            // then
            assertThat(result).isInstanceOf(Result.Error::class)
            assertThat((result as Result.Error).error).isInstanceOf(BotError.Unknown::class)
        }
    }
    //endregion
}