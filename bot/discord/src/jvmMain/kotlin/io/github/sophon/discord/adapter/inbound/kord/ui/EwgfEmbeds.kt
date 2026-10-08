package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.app.model.response.EwgfResponse

internal fun ewgfResponseEmbed(ewgfResponse: EwgfResponse): EmbedBuilder.() -> Unit {
    val embedBuilder = when (ewgfResponse) {
        is EwgfResponse.RecentSets -> recentSetsEmbed(ewgfResponse)
        is EwgfResponse.Success -> successEmbed(ewgfResponse)
        is EwgfResponse.Help -> ewgfHelpEmbed(ewgfResponse)
    }
    return embedBuilder
}

internal fun recentSetsEmbed(
    recentSets: EwgfResponse.RecentSets,
): EmbedBuilder.() -> Unit = {
    title = "${recentSets.playerName}: ${recentSets.playerRank}"
    color = recentSets.dataSource.color?.let { Color(it) }
    url = recentSets.profileUrl

    recentSets.setList
        .chunked(SETS_PER_FIELD)
        .forEach { columnSetList ->
            val columnString = columnSetList.joinToString("") { it.toColumn() }
            mandatoryField(
                name = "",
                value = columnString,
            )
        }

    featureFooter(recentSets.dataSource)
}

internal fun successEmbed(
    success: EwgfResponse.Success,
): EmbedBuilder.() -> Unit = {
    title = "Success"
    color = success.dataSource.color?.let { Color(it) }

    mandatoryField(
        name = "",
        value = success.operation::class.simpleName,
    )

    featureFooter(success.dataSource)
}

internal fun ewgfHelpEmbed(
    help: EwgfResponse.Help,
): EmbedBuilder.() -> Unit = {
    title = "How to use EWGF"
    color = help.dataSource.color?.let { Color(it) }

    mandatoryField(
        name = "",
        value = "1. register: `@bot ewgf + {your Tekken ID}` or `/ewgf + {your Tekken ID}`\n" +
                "   - Tekken ID is the 12-character code; found on [**ewgf.gg**](https://ewgf.gg/) or in the game\n" +
                "   - can also be used to update with new Tekken ID\n" +
                "2. show sets: `@bot ewgf` or `/ewgf`\n\n" +
                "- to unregister: `@bot ewgf -` or `/ewgf -`\n" +
                "- to search: `@bot ewgf @user` or `/ewgf @user`"
    )

    featureFooter(help.dataSource)
}

private fun EwgfResponse.RecentSets.BattleSet.toColumn(): String {
    val opponentLink = "[${this.opponentName}](${this.opponentProfileUrl})"

    val summary = "* **${this.playerScore}-${this.opponentScore}**: " +
            "${this.playerCharacter} v ${this.opponentCharacter} ($opponentLink); " +
            this.battleType

    val matchup = this.outcomeList.joinToString("") { outcome ->
        when (outcome) {
            EwgfResponse.RecentSets.Outcome.WIN -> "🟢"
            EwgfResponse.RecentSets.Outcome.LOSE -> "🔴"
            EwgfResponse.RecentSets.Outcome.DRAW -> "🟡"
        }
    }

    val column = "$summary\n   * $matchup\n"
    return column
}


private const val SETS_PER_FIELD = 9
