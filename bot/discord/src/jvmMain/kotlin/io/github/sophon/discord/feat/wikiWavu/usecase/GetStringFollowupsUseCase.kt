package io.github.sophon.discord.feat.wikiWavu.usecase

import dev.kord.common.Color
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.ui.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import kotlin.time.Duration.Companion.seconds

internal class GetStringFollowupsUseCase(
    private val getMovesUseCase: GetMovesUseCase,
) {
    suspend fun invoke(
        wiki: WikiClient,
        query: String,
        featureInfo: FeatureInfo,
    ): Result<BotOutput, BotError> {
        val parts = query.split(' ').apply {
            if (size < 2) return Result.Error(BotError.InvalidQuery("charName startingMove"))
        }
        val charName = parts.first()
        val startingMoveInput = parts.drop(1).joinToString()
        return getMovesUseCase(
            wiki = wiki,
            characterQuery = charName,
            filter = TekkenFilters.Strings(startingMoveInput),
        ).map { (character, moveList) ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${character.displayName.uppercase()} ${startingMoveInput.uppercase()} Followups",
                    dataList = moveList.map {
                        "${it.input}: ${it.guard}"
                    },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName = character.id),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
    }


    private companion object {
        private const val BLUE = 0x00095FB
    }
}