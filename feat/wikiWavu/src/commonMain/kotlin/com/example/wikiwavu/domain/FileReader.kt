package com.example.wikiwavu.domain

interface FileReader {
    fun readFile(path: String): String
}