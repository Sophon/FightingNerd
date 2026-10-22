package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.DeletePlayerPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UnregisterPlayerServiceTest {
    @Test
    fun `unregister deletes only that player`() = runTest {
        // given
        val expected = mapOf(OTHER_DISCORD_ID to otherPlayer)
        val deletePlayerPort = FakeDeletePlayerPort(player, otherPlayer)
        val service = UnregisterPlayerService(deletePlayerPort = deletePlayerPort)

        // when
        service.invoke(DISCORD_ID)

        // then
        assertThat(deletePlayerPort.playerMap).isEqualTo(expected)
    }

    @Test
    fun `deleted player is a success`() = runTest {
        // given
        val service = UnregisterPlayerService(deletePlayerPort = FakeDeletePlayerPort(player))

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `failed delete is a database error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.Database(DataError.Local.UNKNOWN))
        val service = UnregisterPlayerService(
            deletePlayerPort = FakeDeletePlayerPort(player, error = DataError.Local.UNKNOWN),
        )

        // when
        val result = service.invoke(DISCORD_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeDeletePlayerPort(
        vararg storedPlayers: Player,
        private val error: DataError? = null,
    ): DeletePlayerPort {
        val playerMap = storedPlayers.associateBy { it.discordId }.toMutableMap()

        override suspend fun delete(discordId: String): EmptyResult<DataError> {
            if (error != null) return Result.Error(error)

            playerMap.remove(discordId)
            return Result.Success(Unit)
        }
    }
}


private const val DISCORD_ID = "111111111111111111"
private const val OTHER_DISCORD_ID = "222222222222222222"
private val player = Player(polarisId = "2Edf6ArhMm3J", discordId = DISCORD_ID, name = "Sophon")
private val otherPlayer = Player(polarisId = "4Rn72dMmqQyN", discordId = OTHER_DISCORD_ID, name = "Heihachan")
