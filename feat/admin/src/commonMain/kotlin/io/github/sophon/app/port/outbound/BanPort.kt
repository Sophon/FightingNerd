package io.github.sophon.app.port.outbound

import io.github.sophon.app.domain.model.Ban
import io.github.sophon.app.domain.model.ModerationRequest
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result

internal interface BanPort {
    suspend fun ban(moderationRequest: ModerationRequest): Result<Ban, DataError>
    suspend fun unban(moderationRequest: ModerationRequest): Result<Ban, DataError>
}
