package io.github.sophon.glossaryinfil.adapter.outbound.ktor

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.glossaryinfil.app.outPort.FetchGlossaryPort
import io.github.sophon.glossaryinfil.model.GlossaryItem
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal class KtorGlossaryAdapter(
    private val httpClient: HttpClient,
): FetchGlossaryPort {
    override suspend fun fetch(): Result<List<GlossaryItem>, DataError.Remote> {
        val glossaryResult = safeCall<List<GlossaryItemDto>> { httpClient.get(GLOSSARY_URL) }
            .map { dtoList -> dtoList.map { dto -> dto.toDomain() } }
        return glossaryResult
    }


    private companion object {
        const val GLOSSARY_URL = "https://glossary.infil.net/json/glossary.json"
    }
}
