package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.core.model.AppError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import okio.FileSystem
import okio.Path

internal interface MediaRepo {
    suspend fun save(gameId: String, media: Move.Urls): EmptyResult<AppError>
    suspend fun wipe(gameId: String)
    suspend fun getLink(gameId: String, media: Move.Urls): Result<List<String>, AppError>
}


internal class MediaRepoImpl(
    private val fs: FileSystem,
    private val baseDir: Path,
    private val http: HttpClient,
) : MediaRepo {
    override suspend fun save(
        gameId: String,
        media: Move.Urls,
    ): EmptyResult<AppError> {
        try {
            val gameDir = baseDir / gameId
            fs.createDirectories(gameDir)
            val urls = listOfNotNull(media.videoUrl) + media.hitboxImageList + media.moveImageList
            urls.forEach { url ->
                val target = gameDir / url.substringAfterLast("/").substringAfterLast(":")
                val bytes = http.get(url).readRawBytes()
                fs.write(target) { write(bytes) }
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(AppError.IOError(e.message.orEmpty()))
        }
    }

    override suspend fun wipe(gameId: String) {
        val gameDir = baseDir / gameId
        fs.deleteRecursively(gameDir, mustExist = false)
    }

    override suspend fun getLink(gameId: String, media: Move.Urls): Result<List<String>, AppError> {
        try {
            val gameDir = baseDir / gameId
            val result = buildList {
                val videoUrl = media.videoUrl
                if (videoUrl != null) {
                    val local = gameDir / videoUrl.substringAfterLast("/").substringAfterLast(":")
                    val link = if (fs.exists(local)) "file://$local" else videoUrl
                    add(link)
                }
                val imageUrls = media.hitboxImageList + media.moveImageList
                imageUrls.forEach { imageUrl ->
                    val local = gameDir / imageUrl.substringAfterLast("/").substringAfterLast(":")
                    val link = if (fs.exists(local)) "file://$local" else imageUrl
                    add(link)
                }
            }
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Error(AppError.IOError(e.message.orEmpty()))
        }
    }
}
