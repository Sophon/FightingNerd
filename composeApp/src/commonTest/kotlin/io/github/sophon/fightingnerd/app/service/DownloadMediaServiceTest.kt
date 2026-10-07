package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DownloadMediaServiceTest {
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
    private val electric = Move(
        input = "f,n,d,d/f+2",
        urls = Move.Urls(
            wikiUrl = "https://wavu.wiki/t/Jin_movelist#Jin-f,n,d,df+2",
            moveImageList = listOf(
                "https://wavu.wiki/images/Jin-ewhf-1.png",
                "https://wavu.wiki/images/Jin-ewhf-2.png",
            ),
        ),
        groupId = "Motion input",
    )

    @Test
    fun `running media count is emitted after every move`() = runTest {
        // given
        val service = DownloadMediaService(FakeMediaPort())
        val expected = listOf(2, 2, 4)

        // when
        val result = service("Tekken_8", "jin", listOf(jab, oneTwo, electric)).toList()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `media of every move is saved for the character`() = runTest {
        // given
        val mediaPort = FakeMediaPort()
        val service = DownloadMediaService(mediaPort)
        val expected = listOf(
            Triple("Tekken_8", "jin", jab.urls),
            Triple("Tekken_8", "jin", electric.urls),
        )

        // when
        service("Tekken_8", "jin", listOf(jab, electric)).toList()

        // then
        assertThat(mediaPort.savedList).isEqualTo(expected)
    }


    private class FakeMediaPort: MediaPort {
        val savedList = mutableListOf<Triple<String, String, Move.Urls>>()

        override suspend fun save(gameId: String, characterId: String, urls: Move.Urls): EmptyResult<AppError> {
            savedList.add(Triple(gameId, characterId, urls))
            return Result.Success(Unit)
        }

        override fun subscribeToCharactersWithOfflineMedia(gameId: String): Flow<Set<String>> {
            return emptyFlow()
        }

        override suspend fun wipe(gameId: String, characterId: String): EmptyResult<AppError> {
            error("not used")
        }

        override fun toOfflineUrls(gameId: String, characterId: String, urls: Move.Urls): Move.Urls {
            error("not used")
        }
    }
}
