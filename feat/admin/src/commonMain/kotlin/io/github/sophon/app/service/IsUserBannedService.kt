package io.github.sophon.app.service

import io.github.sophon.app.outPort.BanPort
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.inPort.IsUserBannedUseCase
import io.github.sophon.model.AdminError
import kotlin.time.Clock

internal class IsUserBannedService(
    private val banPort: BanPort,
    private val clock: Clock,
): IsUserBannedUseCase {
    override suspend fun invoke(userId: String): Result<Boolean, AdminError> {
        val now = clock.now()
        val result = banPort.getBan(userId)
            .map { ban ->
                val isBanned = ((ban != null) && (ban.expiresAt > now))
                isBanned
            }
            .mapError { error -> AdminError.Database(error) }
        return result
    }
}
