package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.MediaResponse

internal interface MediaService {
    suspend fun findMedia(query: String): Result<MediaResponse, BotError>
}


internal class MediaServiceImpl(
    private val moveService: MoveService,
): MediaService {
    override suspend fun findMedia(query: String): Result<MediaResponse, BotError> {
        val result = moveService.findMove(query)
            .map { move ->
                when {
                    (move.videoUrl != null) -> {
                        MediaResponse.VideoMediaResponse(
                            characterName = move.characterName,
                            input = move.input,
                            videoUrl = move.videoUrl,
                            dataSource = move.dataSource,
                        )
                    }
                    move.hitboxImageList.isNotEmpty() -> {
                        MediaResponse.ImagesMediaResponse(
                            characterName = move.characterName,
                            input = move.input,
                            url = move.characterUrl,
                            imageList = move.hitboxImageList,
                            dataSource = move.dataSource,
                        )
                    }
                    move.imageList.isNotEmpty() -> {
                        MediaResponse.ImagesMediaResponse(
                            characterName = move.characterName,
                            input = move.input,
                            url = move.characterUrl,
                            imageList = move.imageList,
                            dataSource = move.dataSource,
                        )
                    }
                    else -> MediaResponse.NoMedia
                }
            }
        return result
    }
}