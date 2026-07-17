package io.github.sophon.discord.feat.bot.usecase

import dev.kord.core.event.message.MessageCreateEvent
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope

internal class HandleMessageUseCase(
    private val resultToEmbedUseCase: ResultToEmbedUseCase,
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
    private val coroutineScope: CoroutineScope,
) {
    suspend fun MessageCreateEvent.invoke(
        editableEmbedMap: MutableMap<String, BotOutput>,
    )  {
        // ignoring other bots, even ourselves
        if (message.author?.isBot != false) return

        // ignoring if someone replies with tag
        val botId = kord.selfId
        val botMention = "<@$botId>"
        val botNicknameMention = "<@!$botId>"
        if (botMention !in message.content && botNicknameMention !in message.content) {
            return
        }

        handleMessage(editableEmbedMap)
    }

    private suspend fun MessageCreateEvent.handleMessage(
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        if (kord.selfId !in message.mentionedUserIds) return

        val source = Source(
            username = message.author?.username.orEmpty(),
            id = message.author?.id.toString(),
            channelId = message.channelId.toString(),
            serverName = message.getGuildOrNull()?.name.orEmpty(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source = source,
            message = message.content,
        )

        with (resultToEmbedUseCase) {
            invoke(source, result, coroutineScope, editableEmbedMap)
        }
    }
}