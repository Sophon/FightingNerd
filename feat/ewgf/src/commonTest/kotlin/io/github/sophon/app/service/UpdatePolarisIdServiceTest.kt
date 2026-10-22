package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.UpdatePolarisIdPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdatePolarisIdServiceTest {
    @Test
    fun `polaris id is updated with the dashes stripped`() = runTest {
        // given
        val expected = mapOf(DISCORD_ID to "4Rn72dMmqQyN")
        val updatePolarisIdPort = FakeUpdatePolarisIdPort()
        val service = UpdatePolarisIdService(updatePolarisIdPort = updatePolarisIdPort)

        // when
        service.invoke(Player(polarisId = "4Rn7-2dMm-qQyN", discordId = DISCORD_ID))

        // then
        assertThat(updatePolarisIdPort.polarisIdMap).isEqualTo(expected)
    }

    @Test
    fun `updated polaris id is a success`() = runTest {
        // given
        val service = UpdatePolarisIdService(updatePolarisIdPort = FakeUpdatePolarisIdPort())

        // when
        val result = service.invoke(Player(polarisId = "4Rn7-2dMm-qQyN", discordId = DISCORD_ID))

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
    }

    @Test
    fun `failed update is a database error`() = runTest {
        // given
        val expected = Result.Error(EwgfError.Database(DataError.Local.UNKNOWN))
        val service = UpdatePolarisIdService(
            updatePolarisIdPort = FakeUpdatePolarisIdPort(error = DataError.Local.UNKNOWN),
        )

        // when
        val result = service.invoke(Player(polarisId = "4Rn7-2dMm-qQyN", discordId = DISCORD_ID))

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeUpdatePolarisIdPort(
        private val error: DataError? = null,
    ): UpdatePolarisIdPort {
        val polarisIdMap = mutableMapOf<String, String>()

        override suspend fun update(discordId: String, polarisId: String): EmptyResult<DataError> {
            if (error != null) return Result.Error(error)

            polarisIdMap[discordId] = polarisId
            return Result.Success(Unit)
        }
    }
}


private const val DISCORD_ID = "111111111111111111"
