package io.github.sophon.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.data.BanRepo
import io.github.sophon.integration.AdminFeatureInfo
import io.github.sophon.integration.AdminTool
import io.github.sophon.integration.model.AdminError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Ban
import io.github.sophon.integration.model.Source
import io.github.sophon.usecase.ProcessFeedbackUseCase
import io.github.sophon.usecase.ProcessReplyUseCase
import kotlin.time.Duration

internal class AdminToolImpl(
    private val adminFeatureInfo: AdminFeatureInfo,
    private val repo: BanRepo,
    private val processFeedbackUseCase: ProcessFeedbackUseCase,
    private val processReplyUseCase: ProcessReplyUseCase,
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
        origin: Source,
        target: Source,
        reply: String,
    ): Result<AdminResult, AdminError> {
        return processReplyUseCase.invoke(origin, target, reply, adminConfig)
            .onError { Napier.w(tag = TAG) { "replyToFeedback: $it" } }
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