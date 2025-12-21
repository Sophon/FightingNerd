package io.github.sophon

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.data.BanRepo
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban
import io.github.sophon.usecase.ProcessFeedbackUseCase
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

internal class AdminToolImpl(
    private val adminFeatureInfo: AdminFeatureInfo,
    private val repo: BanRepo,
    private val processFeedbackUseCase: ProcessFeedbackUseCase,
): AdminTool {
    private lateinit var adminConfig: Config.AdminConfig

    override fun getFeatureInfo(): FeatureInfo {
        return adminFeatureInfo.featureInfo
    }

    override fun init(adminConfig: Config.AdminConfig): EmptyResult<AdminError> {
        this.adminConfig = adminConfig
        return Result.Success(Unit)
    }

    override suspend fun processFeedback(
        origin: Source,
        feedback: String,
    ): Result<AdminResult, AdminError> {
        return processFeedbackUseCase.invoke(origin, feedback, adminConfig)
            .onError { error ->
                if (error is AdminError.UserBanned) {
                    Napier.w(tag = TAG) { "Banned: $origin" }
                } else {
                    Napier.e(tag = TAG) { "processFeedback: $error" }
                }
            }
    }

    override fun replyToFeedback(
        target: Source,
        reply: String,
    ): Result<AdminResult, AdminError> {
        return Result.Success(AdminResult(source = target, message = reply))
    }

    override suspend  fun banUser(
        origin: Source,
        offenderId: String,
        duration: Duration,
        preventBotUsage: Boolean
    ): Result<Ban, AdminError> {
        if (adminConfig.administratorIdList.contains(origin.id).not()) {
            return Result.Error(AdminError.PermissionDenied())
        }

        return repo.ban(
            offenderId = offenderId,
            duration = duration,
            authorId = origin.id,
            preventBotUsage = preventBotUsage
        )
            .onSuccess { Napier.i(tag = TAG) { "banUser: $it" } }
    }

    override suspend fun unbanUser(
        origin: Source,
        offenderId: String,
    ): EmptyResult<AdminError> {
        if (adminConfig.administratorIdList.contains(origin.id).not()) {
            return Result.Error(AdminError.PermissionDenied())
        }

        return repo.unban(offenderId)
            .onSuccess { Napier.i(tag = TAG) { "unbanUser: $it" } }
    }

    override suspend fun updateUserPenalty(
        source: Source,
        offenderId: String,
        duration: Duration,
        preventBotUsage: Boolean,
    ): Result<Ban, AdminError> {
        if (adminConfig.administratorIdList.contains(source.id).not()) {
            return Result.Error(AdminError.PermissionDenied())
        }

        return repo.updatePenalty(
            offenderId = offenderId,
            duration = duration,
            authorId = source.id,
            preventBotUsage = preventBotUsage,
        )
            .onSuccess { Napier.i(tag = TAG) { "banUser: $it" } }
    }

    override suspend fun cleanExpiredBans(): EmptyResult<AdminError> {
        return repo.cleanExpiredBans()
    }


    private companion object {
        const val TAG = "AdminTool"
    }
}