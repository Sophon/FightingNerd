package io.github.sophon.discord.infrastructure

import io.github.sophon.wikiwavu.infrastructure.FileReader
import java.io.File

class FileReaderJVM: FileReader {
    override suspend fun readFile(path: String): String {
        return File(path).readText()
    }
}