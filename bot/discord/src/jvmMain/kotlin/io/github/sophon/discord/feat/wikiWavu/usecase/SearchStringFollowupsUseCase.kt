package io.github.sophon.discord.feat.wikiWavu.usecase

import dev.kord.common.Color
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.feat.core.moveListEmbed
import io.github.sophon.discord.feat.core.usecase.GetMovesUseCase
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikiwavu.domain.WavuFilter
import kotlin.time.Duration.Companion.seconds

internal class SearchStringFollowupsUseCase(
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
        return getMovesUseCase.invoke(
            wiki = wiki,
            charName = charName,
            filter = WavuFilter.Strings(startingMoveInput),
        ).map { moveList ->
            BotOutput(
                primaryEmbedBuilder = moveListEmbed(
                    category = "${query.uppercase()} Followups",
                    dataList = moveList.map {
                        "${it.input}: ${it.guard}"
                    },
                    featureInfo = featureInfo,
                    color = Color(BLUE),
                ),
                buttons = BotOutput.ButtonSet(
                    buttonList = moveList.toButtons(charName),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            )
        }
    }


    private companion object {
        private const val BLUE = 0x00095FB
    }
}