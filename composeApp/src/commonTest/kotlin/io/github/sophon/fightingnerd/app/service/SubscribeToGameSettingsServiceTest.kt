package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToGameSettingsServiceTest {
    private val tekken8 = Game(
        id = "Tekken_8",
        displayName = "Tekken 8",
        iconUrl = "https://i.imgur.com/Yl6j809.png",
        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
    )
    private val mbtl = Game(
        id = "MBTL",
        displayName = "Melty Blood: Type Lumina",
        iconUrl = "https://i.imgur.com/E6O7DMi.png",
        wiki = Wiki(name = "Mizuumi Wiki", url = "https://mizuumi.wiki", iconUrl = "https://mizuumi.wiki/mizulogo.png?1fe5d"),
    )

    @Test
    fun `disabled games are kept`() = runTest {
        // given
        val service = SubscribeToGameSettingsService(
            availableGamesPort = FakeAvailableGamesPort(setOf(tekken8, mbtl)),
            subscribeToGameSettingsPort = FakeSubscribeToGameSettingsPort(enabledGameIdSet = setOf("Tekken_8")),
        )
        val expected = Result.Success(mapOf(tekken8 to true, mbtl to false))

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeAvailableGamesPort(
        private val gameSet: Set<Game>,
    ): AvailableGamesPort {
        override fun subscribe(): Flow<Set<Game>> {
            return flowOf(gameSet)
        }
    }

    private class FakeSubscribeToGameSettingsPort(
        private val enabledGameIdSet: Set<String>,
    ): SubscribeToGameSettingsPort {
        override fun subscribeToGameSettings(gameSet: Set<Game>): Flow<Result<Map<Game, Boolean>, AppError>> {
            val result: Result<Map<Game, Boolean>, AppError> = Result.Success(
                gameSet.associateWith { game -> game.id in enabledGameIdSet },
            )
            return flowOf(result)
        }
    }
}
