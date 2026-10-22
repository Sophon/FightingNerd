package io.github.sophon.discord.app.model.response

import io.github.sophon.discord.app.model.EwgfOperation

sealed interface EwgfResponse: BotResponse {
    val dataSource: BotResponse.DataSource

    data class RecentSets(
        override val dataSource: BotResponse.DataSource,
        val playerName: String,
        val playerRank: String,
        val profileUrl: String,
        val setList: List<BattleSet>,
    ): EwgfResponse {
        data class BattleSet(
            val playerCharacter: String,
            val opponentName: String,
            val opponentCharacter: String,
            val opponentProfileUrl: String,
            val playerScore: Int,
            val opponentScore: Int,
            val battleType: String,
            val outcomeList: List<Outcome>,
        )

        enum class Outcome {
            WIN,
            LOSE,
            DRAW,
        }
    }

    data class Success(
        override val dataSource: BotResponse.DataSource,
        val operation: EwgfOperation,
    ): EwgfResponse

    data class Help(
        override val dataSource: BotResponse.DataSource,
    ): EwgfResponse
}
