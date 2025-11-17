package io.github.sophon.discord.featureRegistry.infilGlossary.usecase

import io.github.sophon.glossaryinfil.InfilGlossaryClient

class StartGlossaryUseCase(
    private val glossary: InfilGlossaryClient,
) {
    suspend fun invoke() {
        glossary.downloadGlossary()
    }
}