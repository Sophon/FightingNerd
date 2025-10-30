package com.example.cornerman.screens.home.domain.usecase

import com.example.wikiwavu.WavuWikiClient

class StartWavuSessionUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke() {
        wiki.startSession()
    }
}