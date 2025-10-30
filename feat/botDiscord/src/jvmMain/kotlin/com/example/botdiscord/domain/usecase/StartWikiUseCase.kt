package com.example.botdiscord.domain.usecase

import com.example.wikiwavu.WavuWikiClient

class StartWikiUseCase(
    private val wikiClient: WavuWikiClient,
) {
    suspend fun invoke() {
        wikiClient.startSession()
    }
}