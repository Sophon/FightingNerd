package io.github.sophon.discord.adapter.outbound.glossary

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.GlossaryResponse
import io.github.sophon.glossaryinfil.model.GlossaryError
import io.github.sophon.glossaryinfil.model.GlossaryItem

internal fun FeatureInfo.toDataSource(): BotResponse.DataSource {
    val dataSource = BotResponse.DataSource(
        name = name,
        iconUrl = iconUrl.orEmpty(),
        color = BROWN,
    )
    return dataSource
}

internal fun GlossaryItem.toDomain(dataSource: BotResponse.DataSource): GlossaryResponse {
    val glossaryResponse = GlossaryResponse(
        dataSource = dataSource,
        term = term,
        definition = definition,
        jpTranslationList = jpTranslation,
        termUrl = url.term,
        videoUrl = url.video,
        imageUrl = url.image,
    )
    return glossaryResponse
}

internal fun GlossaryError.toDomainError(): BotError {
    val botError = when (this) {
        is GlossaryError.EmptyGlossary -> BotError.EmptyGlossary()
        is GlossaryError.Download -> BotError.DownloadError(error.toString())
        is GlossaryError.Database -> BotError.DatabaseError()
    }
    return botError
}


private const val BROWN = 0xDAA06D
