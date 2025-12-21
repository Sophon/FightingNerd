package io.github.sophon.data

import io.github.sophon.admin.data.AdminDatabase
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.model.Ban
import io.github.sophon.util.toLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.Duration

internal class BanRepoImpl(
    driver: DatabaseDriverFactory,
): BanRepo {
    private val db = AdminDatabase(driver.createDriver())
    private val queries = db.banQueries

    override suspend fun ban(
        offenderId: String,
        duration: Duration,
        authorId: String,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now()
                val expiresAt = now.plus(duration)
                val ban = Ban(
                    offenderId = offenderId,
                    bannedAt = now,
                    expiresAt = expiresAt,
                    authorId = authorId,
                    preventBotUsage = preventBotUsage,
                )

                queries.upsertBan(
                    offenderId = ban.offenderId,
                    bannedAt = ban.bannedAt.toEpochMilliseconds(),
                    expiresAt = ban.expiresAt.toEpochMilliseconds(),
                    authorId = ban.authorId,
                    preventBotUsage = ban.preventBotUsage.toLong(),
                )
                Result.Success(ban)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun getBanStatus(offenderId: String): Result<Ban?, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val ban = queries.getBan(offenderId)
                    .executeAsOneOrNull()
                    .toDomain()
                Result.Success(ban)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun getBanList(): Result<List<Ban>, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val banList = queries.getActiveBans(now)
                    .executeAsList()
                    .mapNotNull { it.toDomain() }
                Result.Success(banList)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun unban(offenderId: String): EmptyResult<AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                queries.unban(offenderId)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun updatePenalty(
        offenderId: String,
        duration: Duration,
        authorId: String,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now()
                val expiration = now.plus(duration)
                val ban = Ban(
                    offenderId = offenderId,
                    bannedAt = now,
                    expiresAt = expiration,
                    authorId = authorId,
                    preventBotUsage = preventBotUsage,
                )

                queries.updatePenalty(
                    offenderId = ban.offenderId,
                    expiresAt = ban.expiresAt.toEpochMilliseconds(),
                    preventBotUsage = ban.preventBotUsage.toLong(),
                )
                Result.Success(ban)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }

    override suspend fun cleanExpiredBans(): EmptyResult<AdminError.DatabaseError> {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                queries.cleanExpiredBans(now)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(AdminError.DatabaseError(e.toString()))
            }
        }
    }
}
