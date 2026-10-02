package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError

internal interface ReadFilePort {
    fun read(path: String): Result<String, BotError>
}
