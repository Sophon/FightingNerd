package io.github.sophon.botdiscord.adapter.outbound.ewgf

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.adapter.outbound.ewgf.toDataSource
import io.github.sophon.discord.adapter.outbound.ewgf.toDomain
import io.github.sophon.discord.adapter.outbound.ewgf.toDomainError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.model.Battle
import io.github.sophon.model.BattleType
import io.github.sophon.model.Combatant
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Region
import io.github.sophon.model.Score
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import io.github.sophon.model.BattleSet as EwgfBattleSet

class EwgfMappersTest {
    //region toDataSource
    @Test
    fun `feature info becomes a pink data source`() {
        // given
        val featureInfo = FeatureInfo(name = "EWGF", url = FEATURE_URL, version = "1.0.0", iconUrl = ICON_URL)
        val expected = BotResponse.DataSource(name = "EWGF", iconUrl = ICON_URL, color = 0x9F5FF7)

        // when
        val result = featureInfo.toDataSource()

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomain
    @Test
    fun `battle sets become recent sets of the first set's player`() {
        // given
        val setList = listOf(
            battleSet(opponent = opponent, scoreList = listOf(Score(3, 1), Score(1, 3), Score(3, 2)), battleType = BattleType.RANKED),
            battleSet(opponent = otherOpponent, scoreList = listOf(Score(2, 2)), battleType = BattleType.QUICK),
        )
        val expected = EwgfResponse.RecentSets(
            dataSource = dataSource,
            playerName = "Arslan Ash",
            playerRank = "God of Destruction",
            profileUrl = "$FEATURE_URL/player/$PLAYER_POLARIS_ID",
            setList = listOf(
                EwgfResponse.RecentSets.BattleSet(
                    playerCharacter = "Nina",
                    opponentName = "Knee",
                    opponentCharacter = "Bryan",
                    opponentProfileUrl = "$FEATURE_URL/player/$OPPONENT_POLARIS_ID",
                    playerScore = 2,
                    opponentScore = 1,
                    battleType = "RK",
                    outcomeList = listOf(
                        EwgfResponse.RecentSets.Outcome.WIN,
                        EwgfResponse.RecentSets.Outcome.LOSE,
                        EwgfResponse.RecentSets.Outcome.WIN,
                    ),
                ),
                EwgfResponse.RecentSets.BattleSet(
                    playerCharacter = "Nina",
                    opponentName = "Ulsan",
                    opponentCharacter = "Kazuya",
                    opponentProfileUrl = "$FEATURE_URL/player/$OTHER_OPPONENT_POLARIS_ID",
                    playerScore = 0,
                    opponentScore = 0,
                    battleType = "QK",
                    outcomeList = listOf(EwgfResponse.RecentSets.Outcome.DRAW),
                ),
            ),
        )

        // when
        val result = setList.toDomain(dataSource = dataSource, featureUrl = FEATURE_URL)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `no battle sets have a blank player`() {
        // given
        val expected = EwgfResponse.RecentSets(
            dataSource = dataSource,
            playerName = "",
            playerRank = "",
            profileUrl = "$FEATURE_URL/player/",
            setList = emptyList(),
        )

        // when
        val result = emptyList<EwgfBattleSet>().toDomain(dataSource = dataSource, featureUrl = FEATURE_URL)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region toDomainError
    @Test
    fun `unregistered player stays unregistered`() {
        // given
        val error = EwgfError.PlayerNotRegistered

        // when
        val result = error.toDomainError()

        // then
        assertThat(result).isInstanceOf(BotError.PlayerNotRegistered::class)
    }

    @Test
    fun `database error becomes a database error`() {
        // given
        val error = EwgfError.Database(DataError.Local.UNKNOWN)

        // when
        val result = error.toDomainError()

        // then
        assertThat(result).isInstanceOf(BotError.DatabaseError::class)
    }

    @Test
    fun `download error carries the cause`() {
        // given
        val error = EwgfError.Download(DataError.Remote.TOO_MANY_REQUESTS)
        val expected = "DownloadError(TOO_MANY_REQUESTS)"

        // when
        val result = error.toDomainError()

        // then
        assertThat(result.toString()).isEqualTo(expected)
    }
    //endregion
}

/**
 * The set score is the number of battles won - the battle scores are rounds.
 */
private fun battleSet(
    opponent: Combatant,
    scoreList: List<Score>,
    battleType: BattleType,
): EwgfBattleSet {
    val battleList = scoreList.map { score ->
        Battle(
            player = player,
            opponent = opponent,
            score = score,
            battleType = battleType,
            date = date,
            version = GAME_VERSION,
            stageId = STAGE_ID,
        )
    }
    val battleSet = EwgfBattleSet(
        battleList = battleList,
        player = player,
        opponent = opponent,
        score = Score(
            player = scoreList.count { it.outcome == Score.Outcome.WIN },
            opponent = scoreList.count { it.outcome == Score.Outcome.LOSE },
        ),
        battleType = battleType,
        date = date,
        version = GAME_VERSION,
        stageId = STAGE_ID,
    )
    return battleSet
}


private const val FEATURE_URL = "https://ewgf.gg"
private const val ICON_URL = "https://ewgf.gg/favicon.png"
private const val PLAYER_POLARIS_ID = "2Aa4bQ7nJyRf"
private const val OPPONENT_POLARIS_ID = "4gBq9Ld2RmHy"
private const val OTHER_OPPONENT_POLARIS_ID = "3Rt5Hn8Wq2Ke"
private const val GAME_VERSION = 20201
private const val STAGE_ID = 100
private val dataSource = BotResponse.DataSource(name = "EWGF", iconUrl = ICON_URL, color = 0x9F5FF7)
private val date = LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = 0)
private val player = Combatant(
    name = "Arslan Ash",
    polarisId = PLAYER_POLARIS_ID,
    character = "Nina",
    rank = "God of Destruction",
    prowess = 250_000,
    region = Region.ASIA,
)
private val opponent = Combatant(
    name = "Knee",
    polarisId = OPPONENT_POLARIS_ID,
    character = "Bryan",
    rank = "God of Destruction",
    prowess = 240_000,
    region = Region.ASIA,
)
private val otherOpponent = Combatant(
    name = "Ulsan",
    polarisId = OTHER_OPPONENT_POLARIS_ID,
    character = "Kazuya",
    rank = "Tekken God Supreme",
    prowess = 200_000,
    region = Region.ASIA,
)
