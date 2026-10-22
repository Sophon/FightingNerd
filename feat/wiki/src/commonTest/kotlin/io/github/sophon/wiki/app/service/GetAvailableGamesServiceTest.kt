package io.github.sophon.wiki.app.service

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.app.outPort.LoadWikiConfigPort
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetAvailableGamesServiceTest {
    @Test
    fun `disabled games are still available`() = runTest {
        // given
        val configPort = FakeAvailableGamesConfigPort(
            wikiConfig(
                availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                enabledGameSet = setOf(Game.Tekken8),
            )
        )
        val service = GetAvailableGamesService(configPort)
        val expected = setOf(Game.Tekken8, Game.StreetFighter6)

        // when
        val result = service().first()

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `a new config replaces the available games`() = runTest {
        // given
        val configPort = FakeAvailableGamesConfigPort(
            wikiConfig(
                availableGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
                enabledGameSet = setOf(Game.Tekken8, Game.StreetFighter6),
            )
        )
        val service = GetAvailableGamesService(configPort)
        val expected = setOf(Game.StreetFighter6)

        service().test {
            awaitItem()

            // when
            configPort.wikiConfig.value = wikiConfig(
                availableGameSet = setOf(Game.StreetFighter6),
                enabledGameSet = setOf(Game.StreetFighter6),
            )
            val result = awaitItem()

            // then
            assertThat(result).isEqualTo(expected)
        }
    }
}


private fun wikiConfig(
    availableGameSet: Set<Game>,
    enabledGameSet: Set<Game>,
): WikiConfig {
    val result = WikiConfig.create(
        availableGameSet = availableGameSet,
        enabledGameSet = enabledGameSet,
    )
    val wikiConfig = (result as Result.Success).data
    return wikiConfig
}

private class FakeAvailableGamesConfigPort(wikiConfig: WikiConfig?) : LoadWikiConfigPort {
    val wikiConfig = MutableStateFlow(wikiConfig)

    override fun subscribe(): Flow<WikiConfig?> {
        return wikiConfig
    }
}
