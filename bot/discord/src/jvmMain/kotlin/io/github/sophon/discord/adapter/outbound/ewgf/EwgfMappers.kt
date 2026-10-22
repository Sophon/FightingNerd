package io.github.sophon.discord.adapter.outbound.ewgf

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Score
import io.github.sophon.model.BattleSet as EwgfBattleSet

internal fun FeatureInfo.toDataSource(): BotResponse.DataSource {
    val dataSource = BotResponse.DataSource(
        name = name,
        iconUrl = iconUrl.orEmpty(),
        color = PINK,
    )
    return dataSource
}

internal fun List<EwgfBattleSet>.toDomain(
    dataSource: BotResponse.DataSource,
    featureUrl: String,
): EwgfResponse.RecentSets {
    val player = firstOrNull()?.player
    val recentSets = EwgfResponse.RecentSets(
        dataSource = dataSource,
        playerName = player?.name.orEmpty(),
        playerRank = player?.rank.orEmpty(),
        profileUrl = profileUrl(featureUrl = featureUrl, polarisId = player?.polarisId.orEmpty()),
        setList = map { battleSet -> battleSet.toDomain(featureUrl) },
    )
    return recentSets
}

internal fun EwgfError.toDomainError(): BotError {
    val botError = when (this) {
        is EwgfError.PlayerNotRegistered -> BotError.PlayerNotRegistered()
        is EwgfError.Database -> BotError.DatabaseError()
        is EwgfError.Download -> BotError.DownloadError(error.toString())
    }
    return botError
}


private fun EwgfBattleSet.toDomain(featureUrl: String): EwgfResponse.RecentSets.BattleSet {
    val battleSet = EwgfResponse.RecentSets.BattleSet(
        playerCharacter = player.character,
        opponentName = opponent.name,
        opponentCharacter = opponent.character,
        opponentProfileUrl = profileUrl(featureUrl = featureUrl, polarisId = opponent.polarisId),
        playerScore = score.player,
        opponentScore = score.opponent,
        battleType = battleType.shortcut,
        outcomeList = battleList.map { battle -> battle.score.outcome.toDomain() },
    )
    return battleSet
}

private fun Score.Outcome.toDomain(): EwgfResponse.RecentSets.Outcome {
    val outcome = when (this) {
        Score.Outcome.WIN -> EwgfResponse.RecentSets.Outcome.WIN
        Score.Outcome.LOSE -> EwgfResponse.RecentSets.Outcome.LOSE
        Score.Outcome.DRAW -> EwgfResponse.RecentSets.Outcome.DRAW
    }
    return outcome
}

private fun profileUrl(featureUrl: String, polarisId: String): String {
    val profileUrl = "$featureUrl/player/$polarisId"
    return profileUrl
}


private const val PINK = 0x9F5FF7
