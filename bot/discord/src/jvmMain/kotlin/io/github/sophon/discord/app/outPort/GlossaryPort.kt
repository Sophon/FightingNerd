package io.github.sophon.discord.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.GlossaryResponse

internal interface GlossaryPort {
    /**
     * Sorted by relevance - the best match first.
     */
    suspend fun search(query: String): Result<List<GlossaryResponse>, BotError>
}
