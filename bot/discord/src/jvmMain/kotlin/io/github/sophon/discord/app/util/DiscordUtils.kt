package io.github.sophon.discord.app.util

import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.request.KtorRequestException
import io.github.aakira.napier.Napier
import io.github.sophon.model.Source

internal suspend fun kordRestCall(
    tag: String,
    source: Source? = null,
    onForbidden: suspend (errorMessage: String?) -> Unit = {},
    block: suspend () -> Unit,
) {
    val prefix = source?.serverName
        ?.takeIf { it.isNotBlank() }
        ?: "(Unknown server)"
    try {
        block()
    } catch (e: KtorRequestException) {
        when (e.status.code) {
            HTTP_FORBIDDEN -> {
                Napier.w(tag = tag) { "⚠️ ${prefix}: Missing permissions: ${e.message}" }
                whisper(tag = tag, prefix = prefix, errorMessage = e.message, onForbidden = onForbidden)
            }

            HTTP_NOT_FOUND -> Napier.w(tag = tag) { "⚠️ ${prefix}: Resource already gone (deleted message?)" }
            else -> Napier.e(tag = tag) { "💥 ${prefix}: Unexpected REST error: ${e.message}" }
        }
    } catch (_: EntityNotFoundException) {
        Napier.w(tag = tag) { "️ ${prefix}: Initial interaction response for interaction was not found" }
    }
}

private suspend fun whisper(
    tag: String,
    prefix: String,
    errorMessage: String?,
    onForbidden: suspend (errorMessage: String?) -> Unit,
) {
    try {
        onForbidden(errorMessage)
    } catch (e: KtorRequestException) {
        Napier.w(tag = tag) { "⚠️ ${prefix}: Whisper failed: ${e.message}" }
    }
}


private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
