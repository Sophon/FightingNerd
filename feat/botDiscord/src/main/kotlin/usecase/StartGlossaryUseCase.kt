package usecase

import InfilGlossary

class StartGlossaryUseCase(
    private val glossary: InfilGlossary,
) {
    suspend fun invoke() {
        glossary.downloadGlossary()
    }
}