package com.example.botdiscord.data

import com.example.wikiwavu.domain.FileReader
import java.io.File

class JVMFileReader: FileReader {
    override fun readFile(path: String): String {
        return File(path).readText()
    }
}