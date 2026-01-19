package io.github.sophon.discord.featureRegistry.core.usecase

import dev.kord.common.Color
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_SCRIPT_LOBBY
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import kotlin.time.Duration.Companion.seconds

internal class CreateJoinEmbedButtonUseCase {

    //steam://joinlobby/586140/109775241137042824/76561198443042808
    //https://Sophon.github.io/lobby.html?target=steam://joinlobby/586140/109775241137042824/76561198443042808
    fun invoke(
        origin: Source,
        query: String,
    ): Result<BotOutput, BotError> {
        val parts = query.split(" ")
        val steamLobbyUrl = parts.first()
        val password = if (parts.size > 1) parts.last() else null

        if (steamLobbyUrl.startsWith("steam://joinlobby/").not())
            return Result.Error(BotError.InvalidSteamLobbyUrl(steamLobbyUrl))

        val url = "$URL_SCRIPT_LOBBY?target=$steamLobbyUrl"
        val userName = origin.serverName.ifBlank { origin.username }

        val botOutput = BotOutput(
            primaryEmbedBuilder = {
                title = "Join $userName's lobby!"
                optionalField(
                    name = "Password",
                    value = "```$password```",
                )
                color = Color(PURPLE)
            },
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "🎮 GLHF",
                        action = BotOutput.EmbedButton.Action.Url(url)
                    )
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(botOutput)
    }


    private companion object {
        const val PURPLE = 0x00A020F0
    }
}