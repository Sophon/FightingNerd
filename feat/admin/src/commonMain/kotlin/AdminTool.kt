import domain.AdminConfig
import domain.AdminError
import domain.AdminFeatureInfo
import domain.AdminResult
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface AdminTool {
    fun getFeatureInfo(): FeatureInfo
    fun init(adminConfig: AdminConfig): EmptyResult<AdminError>

    fun processFeedback(userId: String, feedback: String): Result<AdminResult, AdminError>
    fun replyToFeedback(reply: String): Result<AdminResult, AdminError>

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
}

internal class AdminToolImpl(
    private val adminFeatureInfo: AdminFeatureInfo,
    //usecases
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
        feedback: String
    ): Result<AdminResult, AdminError> {
        TODO("Not yet implemented")
    }

    override fun replyToFeedback(reply: String): Result<AdminResult, AdminError> {
        TODO("Not yet implemented")
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
}