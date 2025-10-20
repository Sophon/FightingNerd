package usecase

import WavuWikiClient

class StartWikiUseCase(
    private val wikiClient: WavuWikiClient,
) {
    suspend fun invoke() {
        wikiClient.downloadCompleteMoveList()
    }
}