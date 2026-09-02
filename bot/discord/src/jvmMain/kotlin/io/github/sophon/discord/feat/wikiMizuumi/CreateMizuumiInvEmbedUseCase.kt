package io.github.sophon.discord.feat.wikiMizuumi

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.toButtons
import io.github.sophon.wikimizuumi.integration.model.MBFilters
import io.github.sophon.wikimizuumi.integration.model.UniFilters
import io.github.sophon.wikimizuumi.integration.model.VSAVFilters
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.seconds

internal class CreateMizuumiInvEmbedUseCase {
    suspend operator fun invoke(
        game: Game,
        wiki: WikiClient,
        featureInfo: FeatureInfo,
        charName: String,
    ): Result<BotOutput, BotError> {
        val filter = when (game) {
            Game.MBTL -> MBFilters.Invincible
            Game.Uni2 -> UniFilters.Invincible
            Game.VSAV -> VSAVFilters.Invincible
            else -> Filter.None
        }

        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(charName)
            ?: return Result.Error(BotError.UnknownCharacter(charName))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)
            .distinctBy { it.input }

        val botOutput = BotOutput(
            primaryEmbedBuilder = mizuumiMoveListEmbed(
                featureInfo = featureInfo,
                category = "${character.displayName.uppercase()} Inv",
                moveList = moveList,
            ),
            buttons = BotOutput.ButtonSet(
                buttonList = moveList.toButtons(charName = character.id),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )
        val result = Result.Success(botOutput)
        return result
    }
}
