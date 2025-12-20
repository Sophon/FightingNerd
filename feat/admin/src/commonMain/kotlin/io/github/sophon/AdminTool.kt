package io.github.sophon

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.data.BanRepo
import io.github.sophon.domain.AdminConfig
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.model.Ban
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface AdminTool {
    fun getFeatureInfo(): FeatureInfo

    fun init(adminConfig: AdminConfig): EmptyResult<AdminError>

    fun processFeedback(userId: String, feedback: String): Result<AdminResult, AdminError>

    fun replyToFeedback(userId: String, reply: String): Result<AdminResult, AdminError>

    suspend fun banUser(
        userId: String,
        duration: Duration = 30.toDuration(DurationUnit.DAYS),
        preventBotUsage: Boolean = false,
    ): Result<Ban, AdminError>

    suspend fun unbanUser(userId: String): EmptyResult<AdminError>

    suspend fun updateUserPenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError>

    suspend fun cleanExpiredBans(): EmptyResult<AdminError>
}

internal class AdminToolImpl(
    private val adminFeatureInfo: AdminFeatureInfo,
    private val repo: BanRepo,
): AdminTool {
    private lateinit var adminConfig: AdminConfig

    override fun getFeatureInfo(): FeatureInfo {
        return adminFeatureInfo.featureInfo
    }

    override fun init(adminConfig: AdminConfig): EmptyResult<AdminError> {
        this.adminConfig = adminConfig
        return Result.Success(Unit)
    }

    override fun processFeedback(
        userId: String,
        feedback: String,
    ): Result<AdminResult, AdminError> {
        val result = AdminResult(userId = userId, message = feedback)
        return Result.Success(result)
    }

    override fun replyToFeedback(
        userId: String,
        reply: String
    ): Result<AdminResult, AdminError> {
        val result = AdminResult(userId = userId, message = reply)
        return Result.Success(result)
    }

    override suspend  fun banUser(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean
    ): Result<Ban, AdminError> {
        return repo.ban(userId, duration, preventBotUsage)
            .onSuccess { Napier.i(tag = TAG) { "banUser: $it" } }
    }

    override suspend fun unbanUser(userId: String): EmptyResult<AdminError> {
        return repo.unbanUser(userId)
            .onSuccess { Napier.i(tag = TAG) { "unbanUser: $it" } }
    }

    override suspend fun updateUserPenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError> {
        return repo.updatePenalty(userId, duration, preventBotUsage)
            .onSuccess { Napier.i(tag = TAG) { "banUser: $it" } }
    }

    override suspend fun cleanExpiredBans(): EmptyResult<AdminError> {
        return repo.cleanExpiredBans()
    }


    private companion object {
        const val TAG = "AdminTool"
    }
}