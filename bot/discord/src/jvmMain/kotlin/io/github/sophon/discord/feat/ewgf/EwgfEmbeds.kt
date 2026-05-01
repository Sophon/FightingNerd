package io.github.sophon.discord.feat.ewgf

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.integration.model.BattleSet
import io.github.sophon.integration.model.Score

internal fun recentSetsEmbed(
    setList: List<BattleSet>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    val player = setList.firstOrNull()?.player
    val profileUrl = "${featureInfo.url}/player/${player?.polarisId.orEmpty()}"
    title = "${player?.name.orEmpty()}: ${player?.rank.orEmpty()}"
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

internal fun ewgfHelpEmbed(
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "How to use EWGF"
    color = Color(PINK)

    mandatoryField(
        name = "",
        value = "1. register: `@bot ewgf + {your Tekken ID}` or `/ewgf + {your Tekken ID}`\n" +
                "   - Tekken ID is the 12-character code; found on [**ewgf.gg**](https://ewgf.gg/) or in the game\n" +
                "   - can also be used to update with new Tekken ID\n" +
                "2. show sets: `@bot ewgf` or `/ewgf`\n\n" +
                "- to unregister: `@bot ewgf -` or `/ewgf -`\n" +
                "- to search: `@bot ewgf @user` or `/ewgf @user`"
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