package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.FetchBattleListPort
import io.github.sophon.app.outPort.LoadPlayerPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Battle
import io.github.sophon.model.BattleSet
import io.github.sophon.model.BattleType
import io.github.sophon.model.Combatant
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player
import io.github.sophon.model.Region
import io.github.sophon.model.Score
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test

class GetRecentSetsServiceTest {
    @Test
    fun `unregistered player is an error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.PlayerNotRegistered)
        val service = getRecentSetsService(loadPlayerPort = FakeLoadPlayerPort())

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unregistered player isn't looked up on ewgf`() = runTest {
        // given
        val fetchBattleListPort = FakeFetchBattleListPort()
        val service = getRecentSetsService(
            loadPlayerPort = FakeLoadPlayerPort(),
            fetchBattleListPort = fetchBattleListPort,
        )

        // when
        service.invoke(DISCORD_ID)

        // then
        assertThat(fetchBattleListPort.polarisIdList).isEmpty()
    }

    @Test
    fun `battles are fetched for the player's polaris id`() = runTest {
        // given
        val fetchBattleListPort = FakeFetchBattleListPort()
        val service = getRecentSetsService(fetchBattleListPort = fetchBattleListPort)

        // when
        service.invoke(DISCORD_ID)

        // then
        assertThat(fetchBattleListPort.polarisIdList).containsExactly(PLAYER_POLARIS_ID)
    }

    @Test
    fun `fetched battles are grouped into sets`() = runTest {
        // given
        val latest = battle(opponent = heihachan, at = LocalDateTime(2026, 10, 6, 18, 10))
        val earliest = battle(opponent = kazumishi, at = LocalDateTime(2026, 10, 6, 18, 7))
        val expected = Result.Success(
            listOf(
                BattleSet(
                    battleList = listOf(latest),
                    player = player,
                    opponent = heihachan,
                    score = Score(player = 1, opponent = 0),
                    battleType = BattleType.RANKED,
                    date = LocalDateTime(2026, 10, 6, 18, 10),
                    version = GAME_VERSION,
                    stageId = STAGE_ID,
                ),
                BattleSet(
                    battleList = listOf(earliest),
                    player = player,
                    opponent = kazumishi,
                    score = Score(player = 1, opponent = 0),
                    battleType = BattleType.RANKED,
                    date = LocalDateTime(2026, 10, 6, 18, 7),
                    version = GAME_VERSION,
                    stageId = STAGE_ID,
                ),
            ),
        )
        val service = getRecentSetsService(
            fetchBattleListPort = FakeFetchBattleListPort(battleList = listOf(latest, earliest)),
        )

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed player lookup is a database error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.Database(DataError.Local.UNKNOWN))
        val service = getRecentSetsService(loadPlayerPort = FakeLoadPlayerPort(error = DataError.Local.UNKNOWN))

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed fetch is a download error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.Download(DataError.Remote.TOO_MANY_REQUESTS))
        val service = getRecentSetsService(
            fetchBattleListPort = FakeFetchBattleListPort(error = DataError.Remote.TOO_MANY_REQUESTS),
        )

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeLoadPlayerPort(
        vararg storedPlayers: Player,
        private val error: DataError? = null,
    ): LoadPlayerPort {
        private val playerMap = storedPlayers.associateBy { it.discordId }

        override suspend fun get(discordId: String): Result<Player?, DataError> {
            if (error != null) return Result.Error(error)

            return Result.Success(playerMap[discordId])
        }
    }

    private class FakeFetchBattleListPort(
        private val battleList: List<Battle> = emptyList(),
        private val error: DataError.Remote? = null,
    ): FetchBattleListPort {
        val polarisIdList = mutableListOf<String>()

        override suspend fun fetch(polarisId: String): Result<List<Battle>, DataError.Remote> {
            polarisIdList += polarisId
            if (error != null) return Result.Error(error)

            return Result.Success(battleList)
        }
    }

    private fun getRecentSetsService(
        loadPlayerPort: FakeLoadPlayerPort = FakeLoadPlayerPort(registeredPlayer),
        fetchBattleListPort: FakeFetchBattleListPort = FakeFetchBattleListPort(),
    ): GetRecentSetsService {
        val service = GetRecentSetsService(
            loadPlayerPort = loadPlayerPort,
            fetchBattleListPort = fetchBattleListPort,
        )
        return service
    }
}


private fun battle(opponent: Combatant, at: LocalDateTime): Battle {
    val battle = Battle(
        player = player,
        opponent = opponent,
        score = Score(player = 3, opponent = 1),
        battleType = BattleType.RANKED,
        date = at,
        version = GAME_VERSION,
        stageId = STAGE_ID,
    )
    return battle
}


private const val DISCORD_ID = "111111111111111111"
private const val PLAYER_POLARIS_ID = "2Edf6ArhMm3J"
private const val GAME_VERSION = 20100
private const val STAGE_ID = 6
private val registeredPlayer = Player(polarisId = PLAYER_POLARIS_ID, discordId = DISCORD_ID, name = "Sophon")
private val player = Combatant(
    name = "Sophon",
    polarisId = PLAYER_POLARIS_ID,
    character = "Jin",
    rank = "Tekken King",
    prowess = 198_000,
    region = Region.EUROPE,
)
private val heihachan = Combatant(
    name = "Heihachan",
    polarisId = "4Rn72dMmqQyN",
    character = "Reina",
    rank = "Bushin",
    prowess = 214_000,
    region = Region.ASIA,
)
private val kazumishi = Combatant(
    name = "Kazumishi",
    polarisId = "3gAa4Ry2Jm8E",
    character = "Kazuya",
    rank = "Raijin",
    prowess = 176_000,
    region = Region.AMERICAS,
)
