package io.github.sophon.discord.adapter.outbound.file

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.port.outbound.CheckFileExistsPort
import io.github.sophon.discord.app.port.outbound.CreateFilePort
import io.github.sophon.discord.app.port.outbound.ReadFilePort
import io.github.sophon.discord.app.port.outbound.WriteToFilePort
import io.github.sophon.discord.app.domain.model.BotError
import java.io.File

internal class FilesAdapter: ReadFilePort, WriteToFilePort, CheckFileExistsPort, CreateFilePort {
    override fun read(path: String): Result<String, BotError> {
        val result = try {
            Result.Success(File(path).readText())
        } catch (e: Exception) {
            Result.Error(BotError.FileError(e.toErrorMessage(path)))
        }
        return result
    }

    override fun write(path: String, content: String): EmptyResult<BotError> {
        val result = try {
            File(path).writeText(content)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BotError.FileError(e.toErrorMessage(path)))
        }
        return result
    }

    override fun exists(path: String): Boolean {
        val exists = File(path).exists()
        return exists
    }

    override fun create(path: String): EmptyResult<BotError> {
        val result = try {
            File(path).createNewFile()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(BotError.FileError(e.toErrorMessage(path)))
        }
        return result
    }


    private fun Exception.toErrorMessage(path: String): String {
        val message = "${this.message}: $path"
        return message
    }
}
