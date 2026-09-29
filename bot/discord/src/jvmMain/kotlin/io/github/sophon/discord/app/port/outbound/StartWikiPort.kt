package io.github.sophon.discord.app.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.feat.core.domain.model.BotError

internal interface StartWikiPort {
    suspend fun startWiki(): EmptyResult<BotError>
}
