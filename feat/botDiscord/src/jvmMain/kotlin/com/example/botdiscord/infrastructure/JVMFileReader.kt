package com.example.botdiscord.infrastructure

import com.example.wikiwavu.infrastructure.FileReader
import java.io.File

class JVMFileReader: FileReader {
    override fun readFile(path: String): String {
        return File(path).readText()
    }
}