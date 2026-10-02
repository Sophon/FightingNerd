package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.domain.model.BotError

internal interface WriteToFilePort {
    fun write(path: String, content: String): EmptyResult<BotError>
}
