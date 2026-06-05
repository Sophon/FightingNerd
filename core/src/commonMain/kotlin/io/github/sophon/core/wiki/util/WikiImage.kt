package io.github.sophon.core.wiki.util

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.data.ImageUrlResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

suspend fun getWikiImageUrl(
    httpClient: HttpClient,
    url: String,
    fileNames: List<String>,
): Result<Map<String, String>, DataError.Remote> {
    if (fileNames.isEmpty()) return Result.Success(emptyMap())

    val allResults = mutableMapOf<String, String>()

    // Process in chunks of 50
    fileNames.chunked(50).forEach { chunk ->
        val result = safeCall<ImageUrlResponseDto> {
            httpClient.get(url) {
                parameter("action", "query")
                parameter("titles", chunk.joinToString("|") { "File:$it" })
                parameter("prop", "imageinfo")
                parameter("iiprop", "url")
                parameter("format", "json")
            }
        }.map { response ->
            if (response.query == null) {
                Napier.e(tag = TAG) {
                    "API error: ${response.error?.info} ($url)"
                }
                return@map emptyMap()
            }

            // Build map from normalized titles back to original filenames
            val normalizedMap = response.query.normalized?.associate {
                it.to to it.from.removePrefix("File:")
            } ?: emptyMap()

            response.query.pages.values.mapNotNull { page ->
                if (page.missing != null) return@mapNotNull null
                val url = page.imageinfo?.firstOrNull()?.url ?: return@mapNotNull null
                val originalFileName = normalizedMap[page.title] ?: page.title.removePrefix("File:")
                originalFileName to url
            }.toMap()
        }

        when (result) {
            is Result.Success -> allResults.putAll(result.data)
            is Result.Error -> return result
        }
    }

    return Result.Success(allResults)
}

private const val TAG = "getImageUrl"