package io.github.sophon.discord.feat.core.domain

import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.integration.model.AdminError
import io.github.sophon.domain.EwgfError
import io.github.sophon.domain.StatsError

internal fun WikiError.toDomainError(): BotError {
    return when (this) {
        is WikiError.UnknownCharacter -> BotError.UnknownCharacter(inputs[0])
        is WikiError.UnknownMove -> BotError.UnknownMove(*inputs)
        is WikiError.DownloadError -> BotError.DownloadError(inputs[0])
        is WikiError.DatabaseError -> BotError.Unknown(inputs.getOrNull(0) ?: "")
    }
}

internal fun AdminError.toDomainError(): BotError {
    //TODO: proper mapping
    return BotError.Unknown(this.toString())
}

internal fun EwgfError.toDomainError(): BotError {
    return when (this) {
        is EwgfError.PlayerNotRegistered -> BotError.PlayerNotRegistered()
        is EwgfError.PlayerNotFound -> BotError.DownloadError(this.toString())
        is EwgfError.DatabaseError -> BotError.DatabaseError()
        else -> BotError.Unknown()
    }
}

internal fun StatsError.toDomainError(): BotError {
    return when (this) {
        is StatsError.FileError -> BotError.FileError(*errors)
        else -> BotError.Unknown(errors.toString())
    }
}