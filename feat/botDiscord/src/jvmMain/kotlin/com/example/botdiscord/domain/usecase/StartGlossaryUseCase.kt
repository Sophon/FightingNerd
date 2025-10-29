package com.example.botdiscord.domain.usecase

import com.example.glossaryinfil.InfilGlossary

class StartGlossaryUseCase(
    private val glossary: InfilGlossary,
) {
    suspend fun invoke() {
        glossary.downloadGlossary()
    }
}