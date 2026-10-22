package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToMoveListServiceTest {
    private val jin = Character(id = "jin", displayName = "Jin")
    private val armorKing = Character(id = "armor_king", displayName = "Armor King")
    private val jab = Move(
        input = "1",
        urls = Move.Urls(
            wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1",
            videoUrl = "https://wavu.wiki/images/Jin-1.mp4",
            hitboxImageList = listOf("https://wavu.wiki/images/Jin-1-hitbox.png"),
        ),
        groupId = "n",
    )
    private val oneTwo = Move(
        input = "1,2",
        urls = Move.Urls(wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-1,2"),
        groupId = "n",
    )

    @Test
    fun `character comes with its moves`() = runTest {
        // given
        val service = subscribeToMoveListService(
            characterList = listOf(armorKing, jin),
            moveList = listOf(jab, oneTwo),
        )
        val expected = Result.Success(Pair(jin, listOf(jab, oneTwo)))

        // when
        val result = service("Tekken_8", "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `missing character fails`() = runTest {
        // given
        val service = subscribeToMoveListService(
            characterList = listOf(armorKing),
            moveList = listOf(jab, oneTwo),
        )
        val expected = Result.Error(AppError.WikiError("jin not found"))

        // when
        val result = service("Tekken_8", "jin").first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `downloaded media points to the local file`() = runTest {
        // given
        val localVideoUrl = "file:///data/media/Tekken_8/jin/Jin-1.mp4"
        val service = subscribeToMoveListService(
            characterList = listOf(jin),
            moveList = listOf(jab),
            localUrlByRemoteUrl = mapOf("https://wavu.wiki/images/Jin-1.mp4" to localVideoUrl),
        )
        val expected = jab.urls.copy(videoUrl = localVideoUrl)

        // when
        val result = service("Tekken_8", "jin").first()

        // then
        val urls = (result as? Result.Success)?.data?.second?.single()?.urls
        assertThat(urls).isEqualTo(expected)
    }


    private fun subscribeToMoveListService(
        characterList: List<Character>,
        moveList: List<Move>,
        localUrlByRemoteUrl: Map<String, String> = emptyMap(),
    ): SubscribeToMoveListService {
        val service = SubscribeToMoveListService(
            characterPort = FakeCharacterPort(characterList),
            movePort = FakeMovePort(moveList),
            mediaPort = FakeMediaPort(localUrlByRemoteUrl),
        )
        return service
    }

    private class FakeCharacterPort(
        private val characterList: List<Character>,
    ): CharacterPort {
        override fun subscribeToCharacters(gameId: String): Flow<List<Character>> {
            return flowOf(characterList)
        }
    }

    private class FakeMovePort(
        private val moveList: List<Move>,
    ): MovePort {
        override fun subscribeToMoves(gameId: String, characterId: String): Flow<List<Move>> {
            return flowOf(moveList)
        }
    }

    private class FakeMediaPort(
        private val localUrlByRemoteUrl: Map<String, String>,
    ): MediaPort {
        override fun toOfflineUrls(gameId: String, characterId: String, urls: Move.Urls): Move.Urls {
            val offlineUrls = urls.copy(
                videoUrl = urls.videoUrl?.let { url -> localUrlByRemoteUrl[url] ?: url },
                hitboxImageList = urls.hitboxImageList.map { url -> localUrlByRemoteUrl[url] ?: url },
                moveImageList = urls.moveImageList.map { url -> localUrlByRemoteUrl[url] ?: url },
            )
            return offlineUrls
        }

        override fun subscribeToCharactersWithOfflineMedia(gameId: String): Flow<Set<String>> {
            return emptyFlow()
        }

        override suspend fun save(gameId: String, characterId: String, urls: Move.Urls): EmptyResult<AppError> {
            error("not used")
        }

        override suspend fun wipe(gameId: String, characterId: String): EmptyResult<AppError> {
            error("not used")
        }

        override suspend fun wipe(gameId: String): EmptyResult<AppError> {
            error("not used")
        }
    }
}
