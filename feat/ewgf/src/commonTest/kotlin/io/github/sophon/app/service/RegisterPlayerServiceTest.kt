package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.SavePlayerPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RegisterPlayerServiceTest {
    @Test
    fun `player is saved with the dashes stripped from the polaris id`() = runTest {
        // given
        val expected = Player(polarisId = "2Edf6ArhMm3J", discordId = DISCORD_ID, name = "Sophon")
        val savePlayerPort = FakeSavePlayerPort()
        val service = RegisterPlayerService(savePlayerPort = savePlayerPort)

        // when
        service.invoke(Player(polarisId = "2Edf-6Arh-Mm3J", discordId = DISCORD_ID, name = "Sophon"))

        // then
        assertThat(savePlayerPort.playerList).containsExactly(expected)
    }

    @Test
    fun `saved player is a success`() = runTest {
        // given
        val service = RegisterPlayerService(savePlayerPort = FakeSavePlayerPort())

        // when
        val result = service.invoke(Player(polarisId = "2Edf-6Arh-Mm3J", discordId = DISCORD_ID))

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `failed save is a database error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.Database(DataError.Local.UNKNOWN))
        val service = RegisterPlayerService(savePlayerPort = FakeSavePlayerPort(error = DataError.Local.UNKNOWN))

        // when
        val result = service.invoke(Player(polarisId = "2Edf-6Arh-Mm3J", discordId = DISCORD_ID))

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeSavePlayerPort(
        private val error: DataError? = null,
    ): SavePlayerPort {
        val playerList = mutableListOf<Player>()

        override suspend fun save(player: Player): EmptyResult<DataError> {
            if (error != null) return Result.Error(error)

            playerList += player
            return Result.Success(Unit)
        }
    }
}


private const val DISCORD_ID = "111111111111111111"
