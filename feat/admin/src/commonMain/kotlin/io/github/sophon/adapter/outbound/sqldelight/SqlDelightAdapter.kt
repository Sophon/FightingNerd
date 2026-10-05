package io.github.sophon.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.admin.data.AdminDatabase
import io.github.sophon.app.model.Ban
import io.github.sophon.app.model.ModerationRequest
import io.github.sophon.app.outboundPorts.BanPort
import io.github.sophon.app.outboundPorts.ClearExpiredBansPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.app.util.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock

internal class SqlDelightAdapter(
    driverFactory: DatabaseDriverFactory,
    private val clock: Clock,
): BanPort, ClearExpiredBansPort {
    private val queries = AdminDatabase(driverFactory.createDriver()).banQueries

    override suspend fun ban(moderationRequest: ModerationRequest): Result<Ban, DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                val now = clock.now()
                val ban = Ban(
                    offenderId = moderationRequest.offenderId,
                    bannedAt = now,
                    expiresAt = (now + moderationRequest.duration),
                    issuerId = moderationRequest.authorId,
                    preventBotUsage = moderationRequest.preventBotUsage,
                )

                queries.upsertBan(
                    offenderId = ban.offenderId,
                    bannedAt = ban.bannedAt.toEpochMilliseconds(),
                    expiresAt = ban.expiresAt.toEpochMilliseconds(),
                    authorId = ban.issuerId,
                    preventBotUsage = ban.preventBotUsage.toLong(),
                )
                Result.Success(ban)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "ban(${moderationRequest.offenderId}) failed" }
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
        return result
    }

    override suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<DataError> {
        val result = withContext(Dispatchers.IO) {
            try {
                queries.unban(moderationRequest.offenderId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Napier.e(throwable = e, tag = TAG) { "unban(${moderationRequest.offenderId}) failed" }
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


    private companion object {
        const val TAG = "SqlDelightAdapter"
    }
}
