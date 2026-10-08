package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.PlainTextResponse

internal interface MediaService {
    suspend fun findMedia(query: String): Result<PlainTextResponse, BotError>
}


internal class MediaServiceImpl(
    private val characterService: CharacterService,
    private val moveService: MoveService,
): MediaService {
    override suspend fun findMedia(query: String): Result<PlainTextResponse, BotError> {
        val result = moveService.findFrameData(query)
            .map { move ->
                val string = when {
                    (move.videoUrl != null) -> move.videoUrl
                    move.hitboxImageList.isNotEmpty() -> move.hitboxImageList.joinToString(" ")
                    move.imageList.isNotEmpty() -> move.imageList.joinToString(" ")
                    else -> "No media found"
                }
                PlainTextResponse(text = string)
            }
        return result
    }
}