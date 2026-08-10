package io.github.sophon.core.network

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

suspend inline fun <reified T> safeCall(
    crossinline request: suspend () -> HttpResponse,
): Result<T, DataError.Remote> {
    return withContext(Dispatchers.IO) {
        val response = try {
            request()
        } catch (e: SocketTimeoutException) {
            return@withContext Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: UnresolvedAddressException) {
            return@withContext Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Remote.UNKNOWN)
        }

        return@withContext response.toResult()
    }
}

suspend inline fun <reified T> HttpResponse.toResult(): Result<T, DataError.Remote> {
    val result = when (this.status.value) {
        in 200..299 -> {
            try {
                Result.Success(this.body<T>())
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION_ERROR)
            }
        }
        404 -> Result.Error(DataError.Remote.PAGE_NOT_FOUND)
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER_ERROR)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
    return result
}
