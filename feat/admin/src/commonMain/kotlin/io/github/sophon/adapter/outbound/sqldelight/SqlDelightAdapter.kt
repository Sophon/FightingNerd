package io.github.sophon.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.admin.data.AdminDatabase
import io.github.sophon.model.Ban
import io.github.sophon.app.outPort.BanPort
import io.github.sophon.app.outPort.ClearExpiredBansPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.app.util.toBoolean
import io.github.sophon.app.util.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

internal class SqlDelightAdapter(
    driverFactory: DatabaseDriverFactory,
    private val clock: Clock,
): BanPort, ClearExpiredBansPort {
    private val queries = AdminDatabase(driverFactory.createDriver()).banQueries

    override suspend fun ban(ban: Ban): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.upsertBan(
                    offenderId = ban.offenderId,
                    bannedAt = ban.bannedAt.toEpochMilliseconds(),
                    expiresAt = ban.expiresAt.toEpochMilliseconds(),
                    authorId = ban.issuerId,
                    preventBotUsage = ban.preventBotUsage.toLong(),
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "save(${ban.offenderId}) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun unban(offenderId: String): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.unban(offenderId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "delete($offenderId) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun getBan(offenderId: String): Result<Ban?, DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                val ban = queries.getBan(offenderId, ::toBan).executeAsOneOrNull()
                Result.Success(ban)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "getBan($offenderId) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun clear(): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.cleanExpiredBans(clock.now().toEpochMilliseconds())
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "clear() failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }


    private fun toBan(
        offenderId: String,
        bannedAt: Long,
        expiresAt: Long,
        authorId: String,
        preventBotUsage: Long,
    ): Ban {
        val ban = Ban(
            offenderId = offenderId,
            bannedAt = Instant.fromEpochMilliseconds(bannedAt),
            expiresAt = Instant.fromEpochMilliseconds(expiresAt),
            issuerId = authorId,
            preventBotUsage = preventBotUsage.toBoolean(),
        )
        return ban
    }


    private companion object {
        const val TAG = "SqlDelightAdapter"
    }
}
