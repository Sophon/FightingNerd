package io.github.sophon.integration

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.integration.model.AdminError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Ban
import io.github.sophon.integration.model.Source
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface AdminTool {
    fun getFeatureInfo(): FeatureInfo

    fun init(adminConfig: Config.AdminConfig): EmptyResult<AdminError>

    suspend fun processFeedback(
        origin: Source,
        feedback: String,
    ): Result<AdminResult, AdminError>

    fun replyToFeedback(
        origin: Source,
        target: Source,
        reply: String,
    ): Result<AdminResult, AdminError>

    suspend fun banUser(
        origin: Source,
        offenderId: String,
        duration: Duration = 30.toDuration(DurationUnit.DAYS),
        preventBotUsage: Boolean = false,
    ): Result<Ban, AdminError>

    suspend fun unbanUser(origin: Source, offenderId: String): EmptyResult<AdminError>

    suspend fun updateUserPenalty(
        source: Source,
        offenderId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError>

    suspend fun cleanExpiredBans(): EmptyResult<AdminError>
}
