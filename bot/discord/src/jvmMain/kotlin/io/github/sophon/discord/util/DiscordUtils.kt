package io.github.sophon.discord.util

import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.request.KtorRequestException
import io.github.aakira.napier.Napier
import io.github.sophon.integration.model.Source

internal suspend fun kordRestCall(
    tag: String,
    source: Source? = null,
    block: suspend () -> Unit,
) {
    val prefix = source?.serverName
        ?.takeIf { it.isNotBlank() }
        ?: "(Unknown server)"
    try {
        block()
    } catch (e: KtorRequestException) {
        when (e.status.code) {
            403 -> Napier.w(tag = tag) { "⚠️ ${prefix}: Missing access (bot not in guild?)" }
            404 -> Napier.w(tag = tag) { "⚠️ ${prefix}: Resource already gone (deleted message?)" }
            else -> Napier.e(tag = tag) { "💥 ${prefix}: Unexpected REST error: ${e.message}" }
        }
    } catch (_: EntityNotFoundException) {
        Napier.w(tag = tag) { "️ ${prefix}: Initial interaction response for interaction was not found" }
    }
}
