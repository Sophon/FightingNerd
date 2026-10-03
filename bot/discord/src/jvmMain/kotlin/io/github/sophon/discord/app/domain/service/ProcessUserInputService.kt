package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.normalizeWhiteSpace
import io.github.sophon.discord.AUTOCOMPLETE_VALUE_DELIMITER
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordCommandInteraction
import io.github.sophon.discord.app.domain.model.Message
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.inbound.ProcessUserInputUseCase
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.Command

/**
 * - flow: [flow-user_input.mmd](../../../docs/flow-user_input.mmd)
 */
internal class ProcessUserInputService(
    private val commandRouterService: CommandRouterService,
): ProcessUserInputUseCase {
    override suspend fun invoke(
        message: Message,
        botId: String,
    ): Result<BotResponse, BotError> {
        if (message.isValid(botId).not()) return Result.Success(BotResponse.Ignore)

        val messageContent = message.content
            .removePrefix(message.findMention(botId).orEmpty())
            .normalizeWhiteSpace()
            .trim()
        val firstWord = messageContent.substringBefore(" ")
        val command = Command.fromId(firstWord)
        val query = if (command == null) {
            messageContent
        } else {
            messageContent.substringAfter(delimiter = " ", missingDelimiterValue = "")
        }

        val userRequest = UserRequest(
            command = command,
            query = query,
            source = UserRequest.Source(
                username = message.author.username,
                id = message.author.id,
                channelId = message.channelId,
                serverName = message.serverName,
            ),
        )

        val result = commandRouterService(userRequest)
        return result
    }

    override suspend fun invoke(
        discordCommandInteraction: DiscordCommandInteraction,
    ): Result<BotResponse, BotError> {
        val command = Command.fromId(discordCommandInteraction.command)
        val query = discordCommandInteraction.argumentMap.toQuery()
        val userRequest = UserRequest(
            command = command,
            query = query,
            source = UserRequest.Source(
                username = discordCommandInteraction.username,
                id = discordCommandInteraction.userId,
                channelId = discordCommandInteraction.channelId,
                serverName = discordCommandInteraction.serverName.orEmpty(),
            ),
        )

        val result = commandRouterService(userRequest)
        return result
    }


    private fun Message.isValid(botId: String): Boolean {
        // ignoring other bots, even ourselves
        if (isFromBot) return false

        // trigger must start with tagging us
        val mention = findMention(botId) ?: return false

        // tag must be followed with a query
        val hasQuery = content.removePrefix(mention).isNotBlank()
        return hasQuery
    }

    private fun Message.findMention(botId: String): String? {
        val mention = listOf("<@$botId>", "<@!$botId>")
            .firstOrNull { content.startsWith(it) }
        return mention
    }

    private fun Map<String, String>.toQuery(): String {
        // character goes first, the wiki resolves the character before the move
        val orderedValueList = (listOfNotNull(get(ARG_CHARACTER)) + filterKeys { it != ARG_CHARACTER }.values)
        val query = orderedValueList
            // autocomplete values are encoded as `value::feature::game`, only the value is the query
            .map { it.substringBefore(AUTOCOMPLETE_VALUE_DELIMITER) }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .normalizeWhiteSpace()
            .trim()
        return query
    }
}


private const val ARG_CHARACTER = "character"
