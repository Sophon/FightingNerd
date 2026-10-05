package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError

internal interface AdminPort {
    fun isUserAdmin(userId: String): Result<Boolean, BotError>
}
