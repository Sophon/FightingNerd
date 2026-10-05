package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError

internal interface ReadFilePort {
    fun read(path: String): Result<String, BotError>
}
