package io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase

import com.example.core.domain.onError
import com.example.core.domain.onSuccess
import com.example.wikiwavu.WavuWikiClient
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

//TODO: refactor - properly handle errors
class StartWavuSessionUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke() {
        wiki.getLastUpdateTimeStamp().onSuccess { instant ->
            when {
                instant == null -> fillDatabase() //DB empty
                instant.isOld() -> {
                    wiki.clearCache()
                    fillDatabase()
                }
            }
        }
            .onError {
                Napier.e(tag = TAG) { "Error: $it" }
            }
    }

    private suspend fun fillDatabase() {
        wiki.apply {
            downloadCharacterList().onSuccess { characterList ->
                characterList.forEach { character ->
                    downloadMoveListFor(character.name).onSuccess { moveList ->
                        cacheMoveList(character, moveList)
                    }
                }
            }
                .onError {
                    Napier.e(tag = TAG) { "Error fill DB: $it" }
                }
        }
    }

    private fun Instant.isOld(): Boolean {
        val now = Clock.System.now()
        val age = now - this
        return age >= UPDATING_PERIOD_HOURS.hours
    }
}

private const val UPDATING_PERIOD_HOURS = 6
private const val TAG = "StartWavuSessionUseCase"