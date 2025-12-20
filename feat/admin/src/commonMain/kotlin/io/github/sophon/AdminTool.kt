package io.github.sophon

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.domain.AdminConfig
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface AdminTool {
    fun getFeatureInfo(): FeatureInfo

    fun init(adminConfig: AdminConfig): EmptyResult<AdminError>

    fun processFeedback(userId: String, feedback: String): Result<AdminResult, AdminError>

    fun replyToFeedback(userId: String, reply: String): Result<AdminResult, AdminError>

    fun banUser(
        userId: String,
        duration: Duration = 30.toDuration(DurationUnit.DAYS),
        preventBotUsage: Boolean = false,
    ): Result<AdminResult, AdminError>

    fun unbanUser(userId: String): Result<AdminResult, AdminError>

    fun updateUserPenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<AdminResult, AdminError>

    fun cleanExpiredBans(): EmptyResult<AdminError>
}

internal class AdminToolImpl(
    private val adminFeatureInfo: AdminFeatureInfo,
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

    override fun banUser(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean
    ): Result<AdminResult, AdminError> {
        TODO("Not yet implemented")
    }

    override fun unbanUser(userId: String): Result<AdminResult, AdminError> {
        TODO("Not yet implemented")
    }

    override fun updateUserPenalty(
        userId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<AdminResult, AdminError> {
        TODO("Not yet implemented")
    }

    override fun cleanExpiredBans(): EmptyResult<AdminError> {
        TODO("Not yet implemented")
    }
}