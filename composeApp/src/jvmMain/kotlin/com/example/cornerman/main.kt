package com.example.cornerman

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.sophon.cornerman.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cornerman",
    ) {
        App()
    }
}