package io.github.sophon.data

import io.github.sophon.data.dto.BattlesDto
import io.github.sophon.integration.model.Battle
import io.github.sophon.integration.model.BattleType
import io.github.sophon.integration.model.Combatant
import io.github.sophon.integration.model.Region
import io.github.sophon.integration.model.Score
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun BattlesDto.toDomain(polarisId: String): List<Battle> {
    return this.data.map { dto ->
        val isP1 = dto.p1TekkenId.equals(polarisId, ignoreCase = true)

        val player = Combatant(
            name = if (isP1) dto.p1Name else dto.p2Name,
            polarisId = if (isP1) dto.p1TekkenId else dto.p2TekkenId,
            character = if (isP1) dto.p1Char else dto.p2Char,
            rank = if (isP1) dto.p1DanRank else dto.p2DanRank,
            prowess = if (isP1) dto.p1TekkenPower else dto.p2TekkenPower,
            region = if (isP1) dto.p1Region.toDomainRegion() else dto.p2Region.toDomainRegion(),
        )

        val opponent = Combatant(
            name = if (isP1) dto.p2Name else dto.p1Name,
            polarisId = if (isP1) dto.p2TekkenId else dto.p1TekkenId,
            character = if (isP1) dto.p2Char else dto.p1Char,
            rank = if (isP1) dto.p2DanRank else dto.p1DanRank,
            prowess = if (isP1) dto.p2TekkenPower else dto.p1TekkenPower,
            region = if (isP1) dto.p2Region.toDomainRegion() else dto.p1Region.toDomainRegion(),
        )

        val score = Score(
            player = if (isP1) dto.p1RoundsWon else dto.p2RoundsWon,
            opponent = if (isP1) dto.p2RoundsWon else dto.p1RoundsWon,
        )

        Battle(
            player = player,
            opponent = opponent,
            score = score,
            battleType = dto.battleType.toDomainType(),
            date = Instant.parse(dto.battleAt).toLocalDateTime(TimeZone.UTC),
            version = dto.gameVersion,
            stageId = dto.stageId,
        )
    }
}

private fun String.toDomainRegion(): Region = when (this) {
    "Asia" -> Region.ASIA
    "Middle East" -> Region.MIDDLE_EAST
    "Oceania" -> Region.OCEANIA
    "Americas" -> Region.AMERICAS
    "Europe" -> Region.EUROPE
    "Region Not Set" -> Region.UNKNOWN
    else -> error("Unknown region: $this")
}

private fun String.toDomainType(): BattleType = when (this) {
    "QUICK_BATTLE" -> BattleType.QUICK
    "RANKED_BATTLE" -> BattleType.RANKED
    "PLAYER_BATTLE" -> BattleType.LOBBY
    "GROUP_BATTLE" -> BattleType.GROUP
    else -> error("Unknown battle type: $this")
}