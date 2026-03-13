package io.github.sophon.discord.featureRegistry.ewgf

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.model.BattleSet
import io.github.sophon.domain.model.Score

internal fun recentSetsEmbed(
    setList: List<BattleSet>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    val player = setList.firstOrNull()?.player
    val profileUrl = "${featureInfo.url}/player/${player?.polarisId.orEmpty()}"
    title = player?.name.orEmpty()
    color = Color(PINK)
    url = profileUrl

    setList
        .chunked(9)
        .forEach { columnSetList ->
            val columnString = columnSetList.joinToString("") { it.toColumn(featureInfo.url) }
            mandatoryField(
                name = "",
                value = columnString,
            )
        }

    featureFooter(featureInfo)
}

internal fun successEmbed(
    operation: EwgfOperations.Operation,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "Success"
    color = Color(PINK)

    mandatoryField(
        name = "",
        value = operation::class.simpleName,
    )

    featureFooter(featureInfo)
}

private fun BattleSet.toColumn(url: String): String {
    val opponentProfileUrl = "${url}/player/${this.opponent.polarisId}"
    val opponentLink = "[${this.opponent.name}]($opponentProfileUrl)"

    val summary = "* **${this.score.player}-${this.score.opponent}**: " +
            "${this.player.character} v ${this.opponent.character} ($opponentLink); " +
            this.battleType.shortcut

    val matchup = this.battleList.joinToString("") { battle ->
        when (battle.score.outcome) {
            Score.Outcome.WIN -> "🟢"
            Score.Outcome.LOSE -> "🔴"
            Score.Outcome.DRAW -> "🟡"
        }
    }

    return "$summary\n   * $matchup\n"
}


private const val PINK = 0x9F5FF7