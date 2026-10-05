package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError

internal interface AdminPort {
    fun isUserAdmin(userId: String): Result<Boolean, BotError>
}
