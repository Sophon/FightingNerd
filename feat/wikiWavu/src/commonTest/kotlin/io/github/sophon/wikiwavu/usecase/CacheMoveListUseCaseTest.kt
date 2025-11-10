package io.github.sophon.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.wikiwavu.domain.model.CharacterMoveList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test

class CacheMoveListUseCaseTest {
    private lateinit var db: FakeMoveListDB
    private lateinit var useCase: CacheMoveListUseCase

    @BeforeTest
    fun setup() {
        db = FakeMoveListDB()
        useCase = CacheMoveListUseCase(db)
    }

    @Test
    fun `successfully caches move list for character name in lowercase`() = runTest {
        // Given
        val character = Character(
            id = "jin",
            displayName = "Jin",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList(),
        )
        val moves = listOf(
            Move(charName = "Jin", id = "1", input = "1"),
            Move(charName = "Jin", id = "2", input = "2")
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val cachedList = db.getCachedMoveList("jin")
        assertThat(cachedList).isNotNull()
        assertThat(cachedList!!.size).isEqualTo(2)
    }

    @Test
    fun `caches move list for each character alias`() = runTest {
        // Given
        val character = Character(
            id = "devil-jin",
            displayName = "Devil Jin",
            queryName = "",
            wikiUrl = "",
            aliasList = listOf("DVJ", "D.Jin"),
        )
        val moves = listOf(
            Move(charName = "Devil Jin", id = "1", input = "1")
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.getCachedMoveList("devil-jin")).isNotNull()
        assertThat(db.getCachedMoveList("DVJ")).isNotNull()
        assertThat(db.getCachedMoveList("D.Jin")).isNotNull()
    }

    @Test
    fun `character name is converted to lowercase before caching`() = runTest {
        // Given
        val character = Character(
            id = "king",
            displayName = "King",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList(),
        )
        val moves = listOf(
            Move(charName = "KING", id = "1", input = "1")
        )

        // When
        useCase.invoke(character, moves)

        // Then
        assertThat(db.getCachedMoveList("king")).isNotNull()
        assertThat(db.getCachedMoveList("KING")).isEqualTo(null)
    }

    @Test
    fun `handles character with no aliases`() = runTest {
        // Given
        val character = Character(
            id = "paul",
            displayName = "Paul",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList()
        )
        val moves = listOf(
            Move(charName = "Paul", id = "df2", input = "d/f+2")
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val cachedList = db.getCachedMoveList("paul")
        assertThat(cachedList).isNotNull()
        assertThat(cachedList!!.size).isEqualTo(1)
    }

    @Test
    fun `handles empty move list`() = runTest {
        // Given
        val character = Character(
            id = "law",
            displayName = "Law",
            queryName = "",
            wikiUrl = "",
            aliasList = listOf("Marshall")
        )
        val characterMoveList = CharacterMoveList(character, emptyList())

        // When
        val result = useCase.invoke(character, listOf())

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.getCachedMoveList("law")).isNotNull()
        assertThat(db.getCachedMoveList("law")!!.size).isEqualTo(0)
        assertThat(db.getCachedMoveList("Marshall")!!.size).isEqualTo(0)
    }

    @Test
    fun `caches moves with their aliases correctly`() = runTest {
        // Given
        val character = Character(
            id = "kazuya",
            displayName = "Kazuya",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList()
        )
        val moves = listOf(
            Move(
                charName = "Kazuya",
                id = "Kazuya-df2",
                input = "df2",
                aliases = listOf("launcher", "df+2")
            )
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        val cachedMoves = db.getCachedMoveList("kazuya")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!.keys).isEqualTo(setOf("df2", "launcher", "df+2"))
    }

    @Test
    fun `all character aliases point to the same move list`() = runTest {
        // Given
        val character = Character(
            id = "steve",
            displayName = "Steve",
            queryName = "",
            wikiUrl = "",
            aliasList = listOf("Steve Fox", "Boxer")
        )
        val moves = listOf(
            Move(charName = "Steve", id = "1", input = "1"),
            Move(charName = "Steve", id = "2", input = "2")
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        val mainList = db.getCachedMoveList("steve")
        val alias1List = db.getCachedMoveList("Steve Fox")
        val alias2List = db.getCachedMoveList("Boxer")

        assertThat(mainList).isNotNull()
        assertThat(alias1List).isNotNull()
        assertThat(alias2List).isNotNull()
        assertThat(mainList!!.size).isEqualTo(2)
        assertThat(alias1List!!.size).isEqualTo(2)
        assertThat(alias2List!!.size).isEqualTo(2)
    }

    @Test
    fun `caches multiple characters independently`() = runTest {
        // Given
        val character1 = Character(
            id = "jin",
            displayName = "Jin",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList()
        )
        val moves1 = listOf(Move(charName = "Jin", id = "1", input = "1"))
        val moveList1 = CharacterMoveList(character1, moves1)

        val character2 = Character(
            id = "kazuya",
            displayName = "Kazuya",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList()
        )
        val moves2 = listOf(Move(charName = "Kazuya", id = "Kazuya-df2", input = "df2"))
        val moveList2 = CharacterMoveList(character2, moves2)

        // When
        useCase.invoke(character1, moves1)
        useCase.invoke(character2, moves2)

        // Then
        assertThat(db.getCachedMoveList("jin")!!.size).isEqualTo(1)
        assertThat(db.getCachedMoveList("kazuya")!!.size).isEqualTo(1)
        assertThat(db.getCachedMoveList("jin")!!["1"]?.input).isEqualTo("1")
        assertThat(db.getCachedMoveList("kazuya")!!["df2"]?.input).isEqualTo("df2")
    }

    @Test
    fun `overwrites existing move list when caching same character again`() = runTest {
        // Given
        val character = Character(
            id = "bryan",
            displayName = "Bryan",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList(),
        )
        val oldMoves = listOf(Move(charName = "Bryan", id = "1", input = "1"))
        val newMoves = listOf(
            Move(charName = "Bryan", id = "2", input = "2"),
            Move(charName = "Bryan", id = "3", input = "3")
        )

        // When
        useCase.invoke(character, oldMoves)
        useCase.invoke(character, newMoves)

        // Then
        val cachedMoves = db.getCachedMoveList("bryan")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!.size).isEqualTo(2)
        assertThat(cachedMoves.keys).isEqualTo(setOf("2", "3"))
    }

    @Test
    fun `handles moves with all optional fields populated`() = runTest {
        // Given
        val character = Character(
            id = "dragunov",
            displayName = "Dragunov",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList(),
        )
        val moves = listOf(
            Move(
                charName = "Dragunov",
                id = "Dragunov-df2",
                input = "df2",
                name = "Uppercut",
                damage = "20",
                startup = "i15",
                recovery = "23",
                onBlock = "-12",
                onHit = "+33g",
                onCH = "Launch",
                notes = listOf("High crush", "Launcher", "cs9~"),
                aliases = listOf("launcher", "uppercut"),
                videoId = "abc123",
                t8Properties = Move.T8Properties(
                    level = "m",
                    isHeat = false,
                    isPowerCrush = false,
                    isHoming = false,
                    isHighCrush = true,
                )
            )
        )

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val cachedMoves = db.getCachedMoveList("dragunov")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!["df2"]?.damage).isEqualTo("20")
        assertThat(cachedMoves["df2"]?.startup).isEqualTo("i15")
        assertThat(cachedMoves["df2"]?.notes?.size).isEqualTo(3)
    }

    @Test
    fun `handles character with portrait and wavu page urls`() = runTest {
        // Given
        val character = Character(
            id = "yoshimitsu",
            displayName = "Yoshimitsu",
            queryName = "",
            wikiUrl = "",
            images = Character.Images(
                bannerUrl = "https://example.com/yoshi.png"
            ),
            aliasList = listOf("Yoshi")
        )
        val moves = listOf(Move(charName = "Yoshimitsu", id = "1", input = "1"))

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.getCachedMoveList("yoshimitsu")).isNotNull()
        assertThat(db.getCachedMoveList("Yoshi")).isNotNull()
    }

    @Test
    fun `always returns success`() = runTest {
        // Given
        val character = Character(
            id = "nina",
            displayName = "Nina",
            queryName = "",
            wikiUrl = "",
            aliasList = emptyList(),
        )
        val moves = listOf(Move(charName = "Nina", id = "1", input = "1"))

        // When
        val result = useCase.invoke(character, moves)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat((result as Result.Success).data).isEqualTo(Unit)
    }

    // Fake implementation for testing
    private class FakeMoveListDB : MoveListDB {
        private val database = mutableMapOf<String, Map<String, Move>>()

        fun getCachedMoveList(charName: String): Map<String, Move>? {
            return database[charName]
        }

        override suspend fun fetchMoveListFor(charName: String): Result<List<Move>, WikiError> {
            return database[charName]
                ?.values?.toList()
                ?.let { Result.Success(it) }
                ?: Result.Error(WikiError.UNKNOWN_CHARACTER)
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WikiError> {
            val moveList = database[charName]
                ?: return Result.Error(WikiError.UNKNOWN_CHARACTER)
            val moveData = moveList[moveQuery]
                ?: return Result.Error(WikiError.UNKNOWN_MOVE)

            return Result.Success(moveData)
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WikiError> {
            return try {
                val indexedMoves = buildMap {
                    moveList.forEach { move ->
                        put(move.input, move)
                        move.aliases.forEach { alias ->
                            put(alias, move)
                        }
                    }
                }
                database[charName] = indexedMoves
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(WikiError.DATABASE_ERROR)
            }
        }

        override suspend fun wipe(): EmptyResult<WikiError> {
            return try {
                database.clear()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(WikiError.DATABASE_ERROR)
            }
        }

        override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
            // Not used in current tests
            return Result.Success(null)
        }
    }
}