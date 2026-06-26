package io.github.sophon.wikiwavu.integration.data

interface FileReader {
    suspend fun readFile(path: String): String
}