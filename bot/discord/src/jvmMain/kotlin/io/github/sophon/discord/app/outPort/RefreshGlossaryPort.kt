package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.model.BotError

internal interface RefreshGlossaryPort {
    suspend fun refresh(): EmptyResult<BotError>
}
