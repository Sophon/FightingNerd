package io.github.sophon.discord.feat.core.data

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import java.io.File

internal class FileManager {
    fun read(path: String): Result<String, BotError> {
        return try {
            Result.Success(File(path).readText())
        } catch (e: Exception) {
            Result.Error(BotError.FileError(path))
        }
    }

    fun write(path: String, content: String): EmptyResult<BotError> {
        return try {
            File(path).writeText(content)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BotError.FileError(path))
        }
    }

    fun exists(path: String): Boolean {
        return File(path).exists()
    }

    fun create(path: String): EmptyResult<BotError> {
        return try {
            File(path).createNewFile()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BotError.FileError(path))
        }
    }
}
