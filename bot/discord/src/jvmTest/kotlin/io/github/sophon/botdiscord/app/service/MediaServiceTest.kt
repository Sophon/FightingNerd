package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.MediaResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.service.MediaServiceImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MediaServiceTest {
    @Test
    fun `video is preferred over images`() = runTest {
        // given
        val move = moveResponse(input = "1,2").copy(
            videoUrl = JAB_VIDEO_URL,
            hitboxImageList = listOf(JAB_HITBOX_IMAGE_URL),
            imageList = listOf(JAB_IMAGE_URL),
        )
        val expected = Result.Success(
            MediaResponse.VideoMediaResponse(
                characterName = "Jin",
                input = "1,2",
                videoUrl = JAB_VIDEO_URL,
                dataSource = move.dataSource,
            ),
        )
        val service = mediaService(Result.Success(move))

        // when
        val result = service.findMedia("jin 1,2")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `hitbox images are preferred over regular images`() = runTest {
        // given
        val move = moveResponse(input = "1,2").copy(
            hitboxImageList = listOf(JAB_HITBOX_IMAGE_URL),
            imageList = listOf(JAB_IMAGE_URL),
        )
        val expected = Result.Success(
            MediaResponse.ImagesMediaResponse(
                characterName = "Jin",
                input = "1,2",
                url = "https://wavu.wiki/t/Jin",
                imageList = listOf(JAB_HITBOX_IMAGE_URL),
                dataSource = move.dataSource,
            ),
        )
        val service = mediaService(Result.Success(move))

        // when
        val result = service.findMedia("jin 1,2")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `regular images are used without video or hitbox images`() = runTest {
        // given
        val move = moveResponse(input = "1,2").copy(imageList = listOf(JAB_IMAGE_URL))
        val expected = Result.Success(
            MediaResponse.ImagesMediaResponse(
                characterName = "Jin",
                input = "1,2",
                url = "https://wavu.wiki/t/Jin",
                imageList = listOf(JAB_IMAGE_URL),
                dataSource = move.dataSource,
            ),
        )
        val service = mediaService(Result.Success(move))

        // when
        val result = service.findMedia("jin 1,2")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `move without media is no media`() = runTest {
        // given
        val expected = Result.Success(MediaResponse.NoMedia)
        val service = mediaService(Result.Success(moveResponse(input = "1,2")))

        // when
        val result = service.findMedia("jin 1,2")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed move lookup is returned`() = runTest {
        // given
        val expected = Result.Error(BotError.UnknownMove())
        val service = mediaService(expected)

        // when
        val result = service.findMedia("jin 1,3")

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `query is passed to the move lookup`() = runTest {
        // given
        val moveService = FakeMoveService()
        val service = MediaServiceImpl(moveService = moveService)

        // when
        service.findMedia("jin 1,2")

        // then
        assertThat(moveService.callList).isEqualTo(listOf("findFrameData(jin 1,2)"))
    }


    private fun mediaService(frameDataResult: Result<MoveResponse, BotError>): MediaServiceImpl {
        val service = MediaServiceImpl(moveService = FakeMoveService(frameDataResult = frameDataResult))
        return service
    }
}


private const val JAB_VIDEO_URL = "https://wavu.wiki/w/images/Jin_1,2.mp4"
private const val JAB_HITBOX_IMAGE_URL = "https://wavu.wiki/w/images/Jin_1,2_hitbox.png"
private const val JAB_IMAGE_URL = "https://wavu.wiki/w/images/Jin_1,2.png"
