package io.github.sophon.discord.feat.wikiWavu

import io.github.sophon.wikiwavu.integration.data.FileReader
import java.io.File

class FileReaderJVM: FileReader {
    override suspend fun readFile(path: String): String {
        return File(path).readText()
    }
}