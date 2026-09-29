package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Runs [write] on [Dispatchers.IO] - the port only sees [DataError.Local], so the exception is logged here.
 */
internal suspend fun runDatabaseWrite(
    tag: String,
    description: String,
    write: () -> Unit,
): EmptyResult<DataError.Local> {
    val result = withContext(Dispatchers.IO) {
        try {
            write()
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(throwable = e, tag = tag) { "$description failed" }
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
    return result
}
