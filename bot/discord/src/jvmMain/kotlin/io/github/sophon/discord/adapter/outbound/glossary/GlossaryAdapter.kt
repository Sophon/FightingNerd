package io.github.sophon.discord.adapter.outbound.glossary

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.discord.app.outPort.GlossaryPort
import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import io.github.sophon.glossaryinfil.GlossaryFeatureInfo
import io.github.sophon.glossaryinfil.inPort.RefreshGlossaryUseCase
import io.github.sophon.glossaryinfil.inPort.SearchGlossaryUseCase

internal class GlossaryAdapter(
    glossaryFeatureInfo: GlossaryFeatureInfo,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val refreshGlossaryUseCase: RefreshGlossaryUseCase,
): GlossaryPort, RefreshGlossaryPort {
    private val dataSource: BotResponse.DataSource = glossaryFeatureInfo.featureInfo.toDataSource()

    override suspend fun search(query: String): Result<List<GlossaryResponse>, BotError> {
        val result = searchGlossaryUseCase(query)
            .map { itemList -> itemList.map { item -> item.toDomain(dataSource) } }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refresh(): EmptyResult<BotError> {
        val result = refreshGlossaryUseCase()
            .mapError { it.toDomainError() }
        return result
    }
}
