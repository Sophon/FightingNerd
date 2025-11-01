package io.github.sophon.wikiwavu.infrastructure

interface FileReader {
    suspend fun readFile(path: String): String
}