package io.github.sophon.cornerman.featureRegistry.wavuWiki

import com.example.wikiwavu.infrastructure.FileReader
import cornerman.composeapp.generated.resources.Res

internal class FileReaderKMP: FileReader {
    override suspend fun readFile(path: String): String {
        return Res.readBytes("files/characters.json")
            .decodeToString()
    }
}