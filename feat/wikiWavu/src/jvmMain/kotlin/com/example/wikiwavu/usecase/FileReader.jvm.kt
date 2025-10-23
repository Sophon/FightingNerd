package com.example.wikiwavu.usecase

import java.io.File

internal actual fun readResourceFile(path: String): String {
    return File(path).readText()
}