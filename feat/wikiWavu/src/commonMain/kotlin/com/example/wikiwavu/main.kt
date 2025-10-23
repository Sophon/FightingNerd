package com.example.wikiwavu
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import org.koin.mp.KoinPlatform.getKoin

suspend fun main() = coroutineScope {
    initKoin()
    Napier.base(DebugAntilog())
    val client = getKoin().get<WavuWikiClient>()

    client.startSession()

    println("========")

    val move = client.frameDataFor(
        charName = "Kazuya",
        moveQuery = "1,1,2"
    )
    println("\n\n\ndata: $move")
}
