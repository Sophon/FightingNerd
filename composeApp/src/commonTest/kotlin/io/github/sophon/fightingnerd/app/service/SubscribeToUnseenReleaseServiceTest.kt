package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.AppVersion
import io.github.sophon.fightingnerd.app.model.Release
import io.github.sophon.fightingnerd.app.outPort.LastSeenReleasePort
import io.github.sophon.fightingnerd.app.outPort.ReleasePort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SubscribeToUnseenReleaseServiceTest {
    private val v102 = appRelease("1.0.2")
    private val v101 = appRelease("1.0.1")
    private val v100 = appRelease("1.0.0")
    private val releaseList = listOf(v102, v101, v100)

    @Test
    fun `emits nothing when last seen version is the newest release`() = runTest {
        // given
        val service = SubscribeToUnseenReleaseService(
            releasePort = FakeReleasePort(Result.Success(releaseList)),
            lastSeenReleasePort = FakeLastSeenReleasePort(lastSeenVersion = "1.0.2"),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = emptyList<Release>()

        // when
        val emissions = service().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `emits the newest release when the last seen version is older`() = runTest {
        // given
        val service = SubscribeToUnseenReleaseService(
            releasePort = FakeReleasePort(Result.Success(releaseList)),
            lastSeenReleasePort = FakeLastSeenReleasePort(lastSeenVersion = "1.0.0"),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = listOf(v102)

        // when
        val emissions = service().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `emits the release matching current version when nothing has been seen`() = runTest {
        // given
        val service = SubscribeToUnseenReleaseService(
            releasePort = FakeReleasePort(Result.Success(releaseList)),
            lastSeenReleasePort = FakeLastSeenReleasePort(lastSeenVersion = null),
            currentVersion = AppVersion("1.0.1"),
        )
        val expected = listOf(v101)

        // when
        val emissions = service().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `filters out pre-releases and bot releases before picking the newest`() = runTest {
        // given
        val botRelease = appRelease("2.0.0").copy(type = Release.Type.BOT)
        val preRelease = appRelease("1.5.0").copy(isPreRelease = true)
        val releaseList = listOf(botRelease, preRelease, v102, v101)
        val service = SubscribeToUnseenReleaseService(
            releasePort = FakeReleasePort(Result.Success(releaseList)),
            lastSeenReleasePort = FakeLastSeenReleasePort(lastSeenVersion = "1.0.1"),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = listOf(v102)

        // when
        val emissions = service().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `emits nothing when the releases fail to load`() = runTest {
        // given
        val service = SubscribeToUnseenReleaseService(
            releasePort = FakeReleasePort(Result.Error(AppError.IOError("NO_INTERNET"))),
            lastSeenReleasePort = FakeLastSeenReleasePort(lastSeenVersion = null),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = emptyList<Release>()

        // when
        val emissions = service().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }
}

private fun appRelease(version: String) = Release(
    version = version,
    isPreRelease = false,
    type = Release.Type.APP,
)

private class FakeReleasePort(
    private val result: Result<List<Release>, AppError>,
) : ReleasePort {
    override suspend fun getReleases(): Result<List<Release>, AppError> = result
}

private class FakeLastSeenReleasePort(
    private val lastSeenVersion: String?,
) : LastSeenReleasePort {
    override fun subscribeToLastSeenVersion(): Flow<String?> = flowOf(lastSeenVersion)
    override suspend fun saveLastSeenVersion(version: String): EmptyResult<AppError> = Result.Success(Unit)
}
