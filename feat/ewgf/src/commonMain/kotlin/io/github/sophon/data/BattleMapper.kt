package io.github.sophon.data

import io.github.sophon.data.dto.BattlesDto
import io.github.sophon.domain.Battle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun BattlesDto.toDomain(): List<Battle> {
    return this.data.map { dto ->
        Battle(
            player = Battle.Combatant(
                name = dto.p2Name,
                polarisId = dto.p2TekkenId,
                character = dto.p2Char,
                rank = dto.p2DanRank,
                prowess = dto.p2TekkenPower,
                region = dto.p2Region.toDomainRegion(),
            ),
            opponent = Battle.Combatant(
                name = dto.p1Name,
                polarisId = dto.p1TekkenId,
                character = dto.p1Char,
                rank = dto.p1DanRank,
                prowess = dto.p1TekkenPower,
                region = dto.p1Region.toDomainRegion(),
            ),
            score = Battle.Score(
                playerRounds = dto.p2RoundsWon,
                opponentRounds = dto.p1RoundsWon,
            ),
            type = dto.battleType.toDomainType(),
            date = Instant.parse(dto.battleAt).toLocalDateTime(TimeZone.UTC),
            version = dto.gameVersion,
            stageId = dto.stageId,
        )
    }
}

private fun String.toDomainRegion(): Battle.Region = when (this) {
    "Asia" -> Battle.Region.ASIA
    "Middle East" -> Battle.Region.MIDDLE_EAST
    "Oceania" -> Battle.Region.OCEANIA
    "Americas" -> Battle.Region.AMERICAS
    "Europe" -> Battle.Region.EUROPE
    else -> error("Unknown region: $this")
}

private fun String.toDomainType(): Battle.Type = when (this) {
    "QUICK_BATTLE" -> Battle.Type.QUICK
    "RANKED_BATTLE" -> Battle.Type.RANKED
    "PLAYER_BATTLE" -> Battle.Type.LOBBY
    else -> error("Unknown battle type: $this")
}