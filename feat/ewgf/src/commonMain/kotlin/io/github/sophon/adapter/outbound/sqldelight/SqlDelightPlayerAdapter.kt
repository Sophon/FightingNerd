package io.github.sophon.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.app.outPort.DeletePlayerPort
import io.github.sophon.app.outPort.LoadPlayerPort
import io.github.sophon.app.outPort.SavePlayerPort
import io.github.sophon.app.outPort.UpdatePolarisIdPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.ewgf.data.EwgfDatabase
import io.github.sophon.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SqlDelightPlayerAdapter(
    driverFactory: EwgfDatabaseDriverFactory,
): LoadPlayerPort, SavePlayerPort, UpdatePolarisIdPort, DeletePlayerPort {
    private val queries = EwgfDatabase(driverFactory.createDriver()).playerQueries

    override suspend fun get(discordId: String): Result<Player?, DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                val player = queries.getPlayerByDiscordId(discordId, ::toPlayer).executeAsOneOrNull()
                Result.Success(player)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "get($discordId) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun save(player: Player): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.upsertPlayer(
                    discordId = player.discordId,
                    polarisId = player.polarisId,
                    name = player.name,
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "save(${player.discordId}) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun update(discordId: String, polarisId: String): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.updatePolarisId(discordId = discordId, polarisId = polarisId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "update($discordId) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun delete(discordId: String): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.delete(discordId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "delete($discordId) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }


    private fun toPlayer(
        discordId: String,
        polarisId: String,
        name: String?,
    ): Player {
        val player = Player(
            polarisId = polarisId,
            discordId = discordId,
            name = name,
        )
        return player
    }


    private companion object {
        const val TAG = "SqlDelightPlayerAdapter"
    }
}
