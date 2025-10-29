package com.example.wikiwavu.infrastructure

interface FileReader {
    fun readFile(path: String): String
}