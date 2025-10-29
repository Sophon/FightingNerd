package com.example.botdiscord.infrastructure

import com.example.wikiwavu.infrastructure.FileReader
import java.io.File

class FileReaderJVM: FileReader {
    override suspend fun readFile(path: String): String {
        return File(path).readText()
    }
}