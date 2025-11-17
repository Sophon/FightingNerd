package io.github.sophon.fightingnerd.featureRegistry.wavuWiki

import fightingnerd.composeapp.generated.resources.Res
import io.github.sophon.wikiwavu.infrastructure.FileReader

internal class FileReaderKMP: FileReader {
    override suspend fun readFile(path: String): String {
        return Res.readBytes("files/characters.json")
            .decodeToString()
    }
}