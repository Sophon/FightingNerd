package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal interface ReviewPolicyRepo {
    fun getInstallationTimestamp(): Flow<Instant?>
    suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<DataError.Local>
}
