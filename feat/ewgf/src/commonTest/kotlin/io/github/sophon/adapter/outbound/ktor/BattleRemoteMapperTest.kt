package io.github.sophon.adapter.outbound.ktor

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.model.Battle
import io.github.sophon.model.BattleType
import io.github.sophon.model.Combatant
import io.github.sophon.model.Region
import io.github.sophon.model.Score
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test

class BattleRemoteMapperTest {
    //region sides
    @Test
    fun `player as p1 keeps p1 as the player`() {
        // given
        val response = battleListResponse(battleDto(p1TekkenId = PLAYER_POLARIS_ID, p2TekkenId = OPPONENT_POLARIS_ID))
        val expected = listOf(
            Battle(
                player = player,
                opponent = opponent,
                score = Score(player = 3, opponent = 1),
                battleType = BattleType.RANKED,
                date = LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 0),
                version = GAME_VERSION,
                stageId = STAGE_ID,
            ),
        )

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `player as p2 swaps the sides`() {
        // given
        val response = battleListResponse(battleDto(p1TekkenId = OPPONENT_POLARIS_ID, p2TekkenId = PLAYER_POLARIS_ID))
        val expected = listOf(
            Battle(
                player = Combatant(
                    name = P2_NAME,
                    polarisId = PLAYER_POLARIS_ID,
                    character = P2_CHARACTER,
                    rank = P2_RANK,
                    prowess = P2_PROWESS,
                    region = Region.ASIA,
                ),
                opponent = Combatant(
                    name = P1_NAME,
                    polarisId = OPPONENT_POLARIS_ID,
                    character = P1_CHARACTER,
                    rank = P1_RANK,
                    prowess = P1_PROWESS,
                    region = Region.EUROPE,
                ),
                score = Score(player = 1, opponent = 3),
                battleType = BattleType.RANKED,
                date = LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 0),
                version = GAME_VERSION,
                stageId = STAGE_ID,
            ),
        )

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `player is matched by polaris id regardless of case`() {
        // given
        val response = battleListResponse(battleDto(p1TekkenId = OPPONENT_POLARIS_ID, p2TekkenId = PLAYER_POLARIS_ID))
        val expected = listOf(PLAYER_POLARIS_ID)

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID.lowercase())

        // then
        assertThat(result.map { battle -> battle.player.polarisId }).isEqualTo(expected)
    }
    //endregion

    //region fields
    @Test
    fun `every battle in the response is mapped in order`() {
        // given
        val response = battleListResponse(
            battleDto(battleAt = "2026-10-06T18:10:00Z"),
            battleDto(battleAt = "2026-10-06T18:07:00Z"),
        )
        val expected = listOf(
            LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 10),
            LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 7),
        )

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result.map { battle -> battle.date }).isEqualTo(expected)
    }

    @Test
    fun `battle time is read in UTC`() {
        // given
        val response = battleListResponse(battleDto(battleAt = "2026-10-06T20:00:00+02:00"))
        val expected = listOf(LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 0))

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result.map { battle -> battle.date }).isEqualTo(expected)
    }

    @Test
    fun `regions map to domain regions`() {
        // given
        val regionList = listOf("Asia", "Middle East", "Oceania", "Americas", "Europe", "Region Not Set")
        val response = battleListResponse(*regionList.map { region -> battleDto(p1Region = region) }.toTypedArray())
        val expected = listOf(
            Region.ASIA,
            Region.MIDDLE_EAST,
            Region.OCEANIA,
            Region.AMERICAS,
            Region.EUROPE,
            Region.UNKNOWN,
        )

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result.map { battle -> battle.player.region }).isEqualTo(expected)
    }

    @Test
    fun `unknown region fails the mapping`() {
        // given
        val response = battleListResponse(battleDto(p1Region = "Antarctica"))

        // when / then
        assertFailure { response.toDomain(PLAYER_POLARIS_ID) }.isInstanceOf(IllegalStateException::class)
    }

    @Test
    fun `battle types map to domain types`() {
        // given
        val battleTypeList = listOf("QUICK_BATTLE", "RANKED_BATTLE", "PLAYER_BATTLE", "GROUP_BATTLE")
        val response = battleListResponse(*battleTypeList.map { type -> battleDto(battleType = type) }.toTypedArray())
        val expected = listOf(BattleType.QUICK, BattleType.RANKED, BattleType.LOBBY, BattleType.GROUP)

        // when
        val result = response.toDomain(PLAYER_POLARIS_ID)

        // then
        assertThat(result.map { battle -> battle.battleType }).isEqualTo(expected)
    }

    @Test
    fun `unknown battle type fails the mapping`() {
        // given
        val response = battleListResponse(battleDto(battleType = "ARCADE_BATTLE"))

        // when / then
        assertFailure { response.toDomain(PLAYER_POLARIS_ID) }.isInstanceOf(IllegalStateException::class)
    }
    //endregion
}


private fun battleListResponse(vararg battleDtoList: BattleDto): BattleListResponseDto {
    val response = BattleListResponseDto(
        metadata = BattleListMetadataDto(
            rateLimitRemaining = 99,
            rateLimitReset = "2026-10-06T19:00:00Z",
            tier = "free",
        ),
        data = battleDtoList.toList(),
    )
    return response
}

private fun battleDto(
    battleAt: String = "2026-10-06T18:00:00Z",
    battleType: String = "RANKED_BATTLE",
    p1TekkenId: String = PLAYER_POLARIS_ID,
    p1Region: String = "Europe",
    p2TekkenId: String = OPPONENT_POLARIS_ID,
): BattleDto {
    val battleDto = BattleDto(
        battleAt = battleAt,
        battleType = battleType,
        gameVersion = GAME_VERSION,
        winner = 1,
        stageId = STAGE_ID,
        p1Name = P1_NAME,
        p1TekkenId = p1TekkenId,
        p1Char = P1_CHARACTER,
        p1Region = p1Region,
        p1TekkenPower = P1_PROWESS,
        p1DanRank = P1_RANK,
        p1RoundsWon = 3,
        p2Name = P2_NAME,
        p2TekkenId = p2TekkenId,
        p2Char = P2_CHARACTER,
        p2Region = "Asia",
        p2DanRank = P2_RANK,
        p2TekkenPower = P2_PROWESS,
        p2RoundsWon = 1,
    )
    return battleDto
}


private const val PLAYER_POLARIS_ID = "2Edf6ArhMm3J"
private const val OPPONENT_POLARIS_ID = "4Rn72dMmqQyN"
private const val GAME_VERSION = 20100
private const val STAGE_ID = 6
private const val P1_NAME = "Sophon"
private const val P1_CHARACTER = "Jin"
private const val P1_RANK = "Tekken King"
private const val P1_PROWESS = 198_000
private const val P2_NAME = "Heihachan"
private const val P2_CHARACTER = "Reina"
private const val P2_RANK = "Bushin"
private const val P2_PROWESS = 214_000
private val player = Combatant(
    name = P1_NAME,
    polarisId = PLAYER_POLARIS_ID,
    character = P1_CHARACTER,
    rank = P1_RANK,
    prowess = P1_PROWESS,
    region = Region.EUROPE,
)
private val opponent = Combatant(
    name = P2_NAME,
    polarisId = OPPONENT_POLARIS_ID,
    character = P2_CHARACTER,
    rank = P2_RANK,
    prowess = P2_PROWESS,
    region = Region.ASIA,
)
