package io.github.sophon.discord.feat.infilGlossary.usecase

import io.github.sophon.glossaryinfil.integration.InfilGlossaryClient

class StartGlossaryUseCase(
    private val glossary: InfilGlossaryClient,
) {
    suspend fun invoke() {
        glossary.downloadGlossary()
    }
}