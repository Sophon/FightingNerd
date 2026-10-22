package io.github.sophon.app.util

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.model.Battle
import io.github.sophon.model.BattleType
import io.github.sophon.model.Combatant
import io.github.sophon.model.Region
import io.github.sophon.model.Score
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test

/**
 * ewgf.gg lists battles newest first, so every `battleList` here starts with the latest battle.
 */
class BattleSetGroupingTest {
    //region grouping
    @Test
    fun `no battles is no sets`() {
        // given
        val battleList = emptyList<Battle>()

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `back-to-back battles against the same opponent are one set`() {
        // given
        val latest = battle(at = time(minute = 10))
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest, earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `different opponent starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10), opponentId = OTHER_OPPONENT_POLARIS_ID)
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `opponent switching character starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10), opponentCharacter = "Kazuya")
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `different battle type starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10), battleType = BattleType.QUICK)
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `different game version starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10), version = (GAME_VERSION + 1))
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `ranked battle on another stage starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10), battleType = BattleType.RANKED, stageId = OTHER_STAGE_ID)
        val earliest = battle(at = time(minute = 7), battleType = BattleType.RANKED)
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `non-ranked battle on another stage stays in the set`() {
        // given
        val latest = battle(at = time(minute = 10), battleType = BattleType.LOBBY, stageId = OTHER_STAGE_ID)
        val earliest = battle(at = time(minute = 7), battleType = BattleType.LOBBY)
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest, earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `battle over five minutes apart starts a new set`() {
        // given
        val latest = battle(at = time(minute = 10))
        val earliest = battle(at = time(minute = 4, second = 59))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest), setOf(earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `battle exactly five minutes apart stays in the set`() {
        // given
        val latest = battle(at = time(minute = 10))
        val earliest = battle(at = time(minute = 5))
        val battleList = listOf(latest, earliest)
        val expected = listOf(setOf(latest, earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }

    @Test
    fun `gap is measured between neighbouring battles, not across the whole set`() {
        // given
        val latest = battle(at = time(minute = 10))
        val middle = battle(at = time(minute = 6))
        val earliest = battle(at = time(minute = 2))
        val battleList = listOf(latest, middle, earliest)
        val expected = listOf(setOf(latest, middle, earliest))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.battleList.toSet() }).isEqualTo(expected)
    }
    //endregion

    //region set details
    @Test
    fun `set score counts won and lost battles, not draws`() {
        // given
        val battleList = listOf(
            battle(at = time(minute = 10), score = WIN),
            battle(at = time(minute = 8), score = DRAW),
            battle(at = time(minute = 6), score = LOSS),
            battle(at = time(minute = 4), score = WIN),
        )
        val expected = listOf(Score(player = 2, opponent = 1))

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.score }).isEqualTo(expected)
    }

    @Test
    fun `set takes the date and stage of its earliest battle`() {
        // given
        val battleList = listOf(
            battle(at = time(minute = 30), battleType = BattleType.QUICK, stageId = OTHER_STAGE_ID),
            battle(at = time(minute = 27), battleType = BattleType.QUICK),
            battle(at = time(minute = 10), battleType = BattleType.QUICK, stageId = OTHER_STAGE_ID),
            battle(at = time(minute = 7), battleType = BattleType.QUICK),
        )
        val expected = listOf(
            time(minute = 27) to STAGE_ID,
            time(minute = 7) to STAGE_ID,
        )

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.map { set -> set.date to set.stageId }).isEqualTo(expected)
    }

    @Test
    fun `finished set lists its battles earliest first`() {
        // given
        val latest = battle(at = time(minute = 30))
        val earliest = battle(at = time(minute = 27))
        val battleList = listOf(
            latest,
            earliest,
            battle(at = time(minute = 7), opponentId = OTHER_OPPONENT_POLARIS_ID),
        )
        val expected = listOf(earliest, latest)

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.first().battleList).isEqualTo(expected)
    }

    @Test
    fun `last set lists its battles earliest first`() {
        // given
        val latest = battle(at = time(minute = 10))
        val earliest = battle(at = time(minute = 7))
        val battleList = listOf(
            battle(at = time(minute = 30), opponentId = OTHER_OPPONENT_POLARIS_ID),
            latest,
            earliest,
        )
        val expected = listOf(earliest, latest)

        // when
        val result = battleList.groupIntoSets()

        // then
        assertThat(result.last().battleList).isEqualTo(expected)
    }
    //endregion
}


private fun battle(
    at: LocalDateTime,
    opponentId: String = OPPONENT_POLARIS_ID,
    opponentCharacter: String = "Reina",
    battleType: BattleType = BattleType.RANKED,
    version: Int = GAME_VERSION,
    stageId: Int = STAGE_ID,
    score: Score = WIN,
): Battle {
    val battle = Battle(
        player = player,
        opponent = Combatant(
            name = "Heihachan",
            polarisId = opponentId,
            character = opponentCharacter,
            rank = "Bushin",
            prowess = 214_000,
            region = Region.ASIA,
        ),
        score = score,
        battleType = battleType,
        date = at,
        version = version,
        stageId = stageId,
    )
    return battle
}

private fun time(minute: Int, second: Int = 0): LocalDateTime {
    val time = LocalDateTime(year = 2026, month = 10, day = 6, hour = 18, minute = minute, second = second)
    return time
}


private const val OPPONENT_POLARIS_ID = "4Rn72dMmqQyN"
private const val OTHER_OPPONENT_POLARIS_ID = "3gAa4Ry2Jm8E"
private const val GAME_VERSION = 20100
private const val STAGE_ID = 6
private const val OTHER_STAGE_ID = 12
private val WIN = Score(player = 3, opponent = 1)
private val LOSS = Score(player = 1, opponent = 3)
private val DRAW = Score(player = 2, opponent = 2)
private val player = Combatant(
    name = "Sophon",
    polarisId = "2Edf6ArhMm3J",
    character = "Jin",
    rank = "Tekken King",
    prowess = 198_000,
    region = Region.EUROPE,
)
