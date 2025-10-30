package com.example.wikiwavu.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.domain.model.CharacterMoveList
import com.example.wikiwavu.domain.model.Move
import kotlinx.coroutines.test.runTest
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
            name = "Jin",
            alias = emptyList()
        )
        val moves = listOf(
            Move(charName = "Jin", id = "1", input = "1"),
            Move(charName = "Jin", id = "2", input = "2")
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

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
            name = "Devil Jin",
            alias = listOf("DVJ", "D.Jin")
        )
        val moves = listOf(
            Move(charName = "Devil Jin", id = "1", input = "1")
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.getCachedMoveList("devil jin")).isNotNull()
        assertThat(db.getCachedMoveList("DVJ")).isNotNull()
        assertThat(db.getCachedMoveList("D.Jin")).isNotNull()
    }

    @Test
    fun `character name is converted to lowercase before caching`() = runTest {
        // Given
        val character = Character(
            name = "KING",
            alias = emptyList()
        )
        val moves = listOf(
            Move(charName = "KING", id = "1", input = "1")
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        useCase.invoke(characterMoveList)

        // Then
        assertThat(db.getCachedMoveList("king")).isNotNull()
        assertThat(db.getCachedMoveList("KING")).isEqualTo(null)
    }

    @Test
    fun `handles character with no aliases`() = runTest {
        // Given
        val character = Character(
            name = "Paul",
            alias = emptyList()
        )
        val moves = listOf(
            Move(charName = "Paul", id = "df2", input = "d/f+2")
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

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
            name = "Law",
            alias = listOf("Marshall")
        )
        val characterMoveList = CharacterMoveList(character, emptyList())

        // When
        val result = useCase.invoke(characterMoveList)

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
            name = "Kazuya",
            alias = emptyList()
        )
        val moves = listOf(
            Move(
                charName = "Kazuya",
                id = "df2",
                input = "d/f+2",
                aliases = listOf("launcher", "df+2")
            )
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        useCase.invoke(characterMoveList)

        // Then
        val cachedMoves = db.getCachedMoveList("kazuya")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!.keys).isEqualTo(setOf("df2", "launcher", "df+2"))
    }

    @Test
    fun `all character aliases point to the same move list`() = runTest {
        // Given
        val character = Character(
            name = "Steve",
            alias = listOf("Steve Fox", "Boxer")
        )
        val moves = listOf(
            Move(charName = "Steve", id = "1", input = "1"),
            Move(charName = "Steve", id = "2", input = "2")
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        useCase.invoke(characterMoveList)

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
        val character1 = Character(name = "Jin", alias = emptyList())
        val moves1 = listOf(Move(charName = "Jin", id = "1", input = "1"))
        val moveList1 = CharacterMoveList(character1, moves1)

        val character2 = Character(name = "Kazuya", alias = emptyList())
        val moves2 = listOf(Move(charName = "Kazuya", id = "df2", input = "d/f+2"))
        val moveList2 = CharacterMoveList(character2, moves2)

        // When
        useCase.invoke(moveList1)
        useCase.invoke(moveList2)

        // Then
        assertThat(db.getCachedMoveList("jin")!!.size).isEqualTo(1)
        assertThat(db.getCachedMoveList("kazuya")!!.size).isEqualTo(1)
        assertThat(db.getCachedMoveList("jin")!!["1"]?.id).isEqualTo("1")
        assertThat(db.getCachedMoveList("kazuya")!!["df2"]?.id).isEqualTo("df2")
    }

    @Test
    fun `overwrites existing move list when caching same character again`() = runTest {
        // Given
        val character = Character(name = "Bryan", alias = emptyList())
        val oldMoves = listOf(Move(charName = "Bryan", id = "1", input = "1"))
        val newMoves = listOf(
            Move(charName = "Bryan", id = "2", input = "2"),
            Move(charName = "Bryan", id = "3", input = "3")
        )

        // When
        useCase.invoke(CharacterMoveList(character, oldMoves))
        useCase.invoke(CharacterMoveList(character, newMoves))

        // Then
        val cachedMoves = db.getCachedMoveList("bryan")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!.size).isEqualTo(2)
        assertThat(cachedMoves.keys).isEqualTo(setOf("2", "3"))
    }

    @Test
    fun `handles moves with all optional fields populated`() = runTest {
        // Given
        val character = Character(name = "Dragunov", alias = emptyList())
        val moves = listOf(
            Move(
                charName = "Dragunov",
                id = "df2",
                input = "d/f+2",
                level = "m",
                name = "Uppercut",
                parent = null,
                damage = "20",
                startup = "i15",
                recoveryOnWhiff = "23",
                totalFrames = "38",
                crushes = listOf("cs9~"),
                onBlock = "-12",
                onHit = "+33g",
                onCH = "Launch",
                notes = listOf("High crush", "Launcher"),
                aliases = listOf("launcher", "uppercut"),
                image = "df2.png",
                videoId = "abc123",
                alt = "Alternative input",
                isHeat = false,
                isPowerCrush = false,
                isHoming = false
            )
        )
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        val cachedMoves = db.getCachedMoveList("dragunov")
        assertThat(cachedMoves).isNotNull()
        assertThat(cachedMoves!!["df2"]?.damage).isEqualTo("20")
        assertThat(cachedMoves["df2"]?.startup).isEqualTo("i15")
        assertThat(cachedMoves["df2"]?.notes?.size).isEqualTo(2)
    }

    @Test
    fun `handles character with portrait and wavu page urls`() = runTest {
        // Given
        val character = Character(
            name = "Yoshimitsu",
            portraitUrl = "https://example.com/yoshi.png",
            wavuPageUrl = "https://wavu.wiki/yoshi",
            alias = listOf("Yoshi")
        )
        val moves = listOf(Move(charName = "Yoshimitsu", id = "1", input = "1"))
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class)
        assertThat(db.getCachedMoveList("yoshimitsu")).isNotNull()
        assertThat(db.getCachedMoveList("Yoshi")).isNotNull()
    }

    @Test
    fun `always returns success`() = runTest {
        // Given
        val character = Character(name = "Nina", alias = emptyList())
        val moves = listOf(Move(charName = "Nina", id = "1", input = "1"))
        val characterMoveList = CharacterMoveList(character, moves)

        // When
        val result = useCase.invoke(characterMoveList)

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

        override suspend fun fetchMoveListFor(charName: String): Result<Map<String, Move>, WavuError> {
            return database[charName]
                ?.let { Result.Success(it) }
                ?: Result.Error(WavuError.UNKNOWN_CHARACTER)
        }

        override suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError> {
            val moveList = database[charName]
                ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
            val moveData = moveList[moveQuery]
                ?: return Result.Error(WavuError.UNKNOWN_MOVE)

            return Result.Success(moveData)
        }

        override suspend fun insertMoveList(charName: String, moveList: List<Move>): EmptyResult<WavuError> {
            return try {
                val indexedMoves = buildMap {
                    moveList.forEach { move ->
                        put(move.id, move)
                        move.aliases.forEach { alias ->
                            put(alias, move)
                        }
                    }
                }
                database[charName] = indexedMoves
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(WavuError.DATABASE_ERROR)
            }
        }

        override suspend fun wipe(): EmptyResult<WavuError> {
            return try {
                database.clear()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(WavuError.DATABASE_ERROR)
            }
        }
    }
}