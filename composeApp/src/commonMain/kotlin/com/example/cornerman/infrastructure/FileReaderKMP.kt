package com.example.cornerman.infrastructure

import com.example.wikiwavu.infrastructure.FileReader
import cornerman.composeapp.generated.resources.Res

class FileReaderKMP: FileReader {
    override suspend fun readFile(path: String): String {
        return Res.readBytes("files/characters.json")
            .decodeToString()
    }
}