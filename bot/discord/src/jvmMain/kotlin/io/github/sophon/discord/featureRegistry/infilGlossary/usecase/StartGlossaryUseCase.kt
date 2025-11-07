package io.github.sophon.discord.featureRegistry.infilGlossary.usecase

import io.github.sophon.glossaryinfil.InfilGlossary

class StartGlossaryUseCase(
    private val glossary: InfilGlossary,
) {
    suspend fun invoke() {
        glossary.downloadGlossary()
    }
}