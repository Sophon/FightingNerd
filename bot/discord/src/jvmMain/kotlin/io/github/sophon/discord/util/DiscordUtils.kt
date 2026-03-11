package io.github.sophon.discord.util

import dev.kord.rest.request.KtorRequestException
import io.github.aakira.napier.Napier

internal suspend fun safeRestCall(tag: String, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: KtorRequestException) {
        when (e.status.code) {
            403 -> Napier.w(tag = tag) { "⚠️ Missing access (bot not in guild?)" }
            404 -> Napier.w(tag = tag) { "⚠️ Resource already gone (deleted message?)" }
            else -> Napier.e(tag = tag) { "💥 Unexpected REST error: ${e.message}" }
        }
    }
}