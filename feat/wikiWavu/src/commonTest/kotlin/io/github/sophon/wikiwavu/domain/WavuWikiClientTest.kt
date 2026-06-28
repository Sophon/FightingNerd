package io.github.sophon.wikiwavu.domain

import assertk.assertThat
import assertk.assertions.containsExactly
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.Move
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wikiwavu.data.CharacterListResponseDto
import io.github.sophon.wikiwavu.data.MoveListResponseDto
import io.github.sophon.wikiwavu.data.WavuWikiDataSource
import kotlinx.datetime.Instant

class WavuWikiClientTest {

    //region fetchMove
    @Test
    fun `fetchMove cleans move query before delegating to moveDB`(): TestResult {
        return runTest {
            // given
            val rawQuery = "ak df1,2"
            val expected = rawQuery.cleanMoveInput(keepSpaces = true)
            val fakeMoveDB = CapturingMoveListDB()
            val client = WavuWikiClient(
                game = Game.Tekken8,
                characterDB = NotImplementedCharacterListDB,
                moveDB = fakeMoveDB,
                source = NotImplementedWavuSource,
            )

            // when
            client.fetchMove(characterId = "armor-king", moveQuery = rawQuery)

            // then
            assertThat(fakeMoveDB.capturedQueries).containsExactly(expected)
        }
    }
    //endregion
}

private class CapturingMoveListDB : MoveListDB {
    val capturedQueries = mutableListOf<String>()

    override suspend fun fetchMoveDataFor(
        characterId: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        capturedQueries.add(moveQuery)
        return Result.Success(STUB_MOVE)
    }

    override suspend fun fetchMoveListFor(characterId: String): Result<List<Move>, WikiError> {
        TODO("not used in this test")
    }

    override suspend fun hasMovesCachedFor(characterId: String): Result<Boolean, WikiError> {
        TODO("not used in this test")
    }

    override suspend fun insertMoveList(
        game: Game?,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WikiError> {
        TODO("not used in this test")
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        TODO("not used in this test")
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        TODO("not used in this test")
    }
}

private object NotImplementedCharacterListDB : CharacterListDB {
    override suspend fun insertCharacterList(characterList: List<Character>): EmptyResult<WikiError> {
        TODO("not used in this test")
    }
    override suspend fun fetchCharacterList(): Result<List<Character>, WikiError> {
        TODO("not used in this test")
    }
    override suspend fun wipe(): EmptyResult<WikiError> {
        TODO("not used in this test")
    }
    override suspend fun fetchCharacterDataFor(characterQuery: String): Result<Character, WikiError> {
        TODO("not used in this test")
    }
}

private object NotImplementedWavuSource : WavuWikiDataSource {
    override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        TODO("not used in this test")
    }
    override suspend fun downloadMoveList(
        table: String,
        character: Character,
    ): Result<MoveListResponseDto, DataError.Remote> {
        TODO("not used in this test")
    }
}

private val STUB_MOVE: Move = Move(
    characterId = "armor-king",
    id = "stub",
    input = "stub",
    urls = Move.Urls(wikiUrl = ""),
)