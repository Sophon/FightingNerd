package com.example.wikiwavu.infrastructure

interface FileReader {
    suspend fun readFile(path: String): String
}