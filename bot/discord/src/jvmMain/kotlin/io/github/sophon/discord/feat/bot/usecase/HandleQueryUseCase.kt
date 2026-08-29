package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.discord.AUTOCOMPLETE_VALUE_DELIMITER
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.integration.model.Source
import kotlinx.coroutines.CoroutineScope

@ExcludeFromCoverage("UI")
internal class HandleQueryUseCase(
    private val resultToEmbedUseCase: ResultToEmbedUseCase,
    private val routeCommandToFeatureUseCase: RouteCommandToFeatureUseCase,
    private val coroutineScope: CoroutineScope,
) {
    //tag command
    suspend fun invoke(
        message: Message,
        botId: Snowflake,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        // ignoring other bots, even ourselves
        if (message.author?.isBot != false) return
        if (message.content.startsWith("<@").not()) return

        // ignoring if someone replies with tag
        val botMention = "<@$botId>"
        val botNicknameMention = "<@!$botId>"
        if (botMention !in message.content && botNicknameMention !in message.content) {
            return
        }

        handleMessage(message, botId, editableEmbedMap)
    }

    //slash command
    suspend fun invoke(
        interaction: GuildChatInputCommandInteraction,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        val commandString = interaction.command.rootName
            .lowercase()
        val routing = extractSlashRouting(interaction.command.strings)
        val source = Source(
            username = interaction.user.username,
            id = interaction.user.data.id.toString(),
            channelId = interaction.channelId.toString(),
            serverName = interaction.getGuildOrNull()?.name.orEmpty(),
        )

        val result = routeCommandToFeatureUseCase.invoke(
            source = source,
            commandString = commandString,
            query = routing.query,
            featureHint = routing.featureName,
            game = routing.game,
        )

        resultToEmbedUseCase.invoke(
            interaction = interaction,
            source = source,
            result = result,
            coroutineScope = coroutineScope,
            editableEmbedMap = editableEmbedMap,
        )
    }

    private fun extractSlashRouting(strings: Map<String, String>): SlashRouting {
        val characterRaw = strings[SLASH_ARG_CHARACTER]
        val encodedParts = characterRaw?.split(AUTOCOMPLETE_VALUE_DELIMITER, limit = 3)
        if (encodedParts != null && encodedParts.size == 3) {
            val (characterId, featureName, gameName) = encodedParts
            val game = Game.entries.firstOrNull { it.name == gameName }
            if (game != null) {
                val restQuery = strings
                    .filterKeys { it != SLASH_ARG_CHARACTER }
                    .values
                    .joinToString(" ") { it.substringBefore(AUTOCOMPLETE_VALUE_DELIMITER) }
                val fullQuery = listOf(characterId, restQuery)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                val routing = SlashRouting(query = fullQuery, featureName = featureName, game = game)
                return routing
            }
        }

        val fallbackQuery = strings.values
            .joinToString(" ") { it.substringBefore(AUTOCOMPLETE_VALUE_DELIMITER) }
        val routing = SlashRouting(query = fallbackQuery, featureName = null, game = null)
        return routing
    }

    private data class SlashRouting(
        val query: String,
        val featureName: String?,
        val game: Game?,
    )

    private suspend fun handleMessage(
        message: Message,
        botId: Snowflake,
        editableEmbedMap: MutableMap<String, BotOutput>,
    ) {
        if (botId !in message.mentionedUserIds) return

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

        resultToEmbedUseCase.invoke(message, source, result, coroutineScope, editableEmbedMap)
    }

    private companion object {
        const val SLASH_ARG_CHARACTER = "character"
    }
}