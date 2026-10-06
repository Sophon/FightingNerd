package io.github.sophon.discord.feat.core.domain

import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.model.AdminError

internal fun WikiError.toDomainError(): BotError {
    return when (this) {
        is WikiError.UnknownCharacter -> BotError.UnknownCharacter(inputs[0])
        is WikiError.UnknownMove -> BotError.UnknownMove(*inputs)
        is WikiError.DownloadError -> BotError.DownloadError(inputs[0])
        is WikiError.PageNotFound -> BotError.DownloadError(inputs[0])
        is WikiError.DatabaseError -> BotError.Unknown(inputs.getOrNull(0) ?: "")
    }
}

internal fun AdminError.toDomainError(): BotError {
    //TODO: proper mapping
    return BotError.Unknown(this.toString())
}
