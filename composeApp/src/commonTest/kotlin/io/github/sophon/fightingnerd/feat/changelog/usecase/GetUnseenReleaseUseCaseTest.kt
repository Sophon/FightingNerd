package io.github.sophon.fightingnerd.feat.changelog.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.ReleaseRepo
import io.github.sophon.fightingnerd.core.model.AppVersion
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetUnseenReleaseUseCaseTest {
    private val v102 = appRelease("1.0.2")
    private val v101 = appRelease("1.0.1")
    private val v100 = appRelease("1.0.0")
    private val releaseList = listOf(v102, v101, v100)

    @Test
    fun `usecase emits nothing when last seen version is the newest release`() = runTest {
        // given
        val useCase = GetUnseenReleaseUseCase(
            releaseRepo = FakeReleaseRepo(lastSeenVersion = "1.0.2", releaseList = releaseList),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = emptyList<Release>()

        // when
        val emissions = useCase.invoke().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits the newest release when the last seen version is older`() = runTest {
        // given
        val useCase = GetUnseenReleaseUseCase(
            releaseRepo = FakeReleaseRepo(lastSeenVersion = "1.0.0", releaseList = releaseList),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = listOf(v102)

        // when
        val emissions = useCase.invoke().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase emits the release matching current version when nothing has been seen`() = runTest {
        // given
        val useCase = GetUnseenReleaseUseCase(
            releaseRepo = FakeReleaseRepo(lastSeenVersion = null, releaseList = releaseList),
            currentVersion = AppVersion("1.0.1"),
        )
        val expected = listOf(v101)

        // when
        val emissions = useCase.invoke().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }

    @Test
    fun `usecase filters out pre-releases and bot releases before picking the newest`() = runTest {
        // given
        val botRelease = appRelease("2.0.0").copy(type = Release.Type.BOT)
        val preRelease = appRelease("1.5.0").copy(isPreRelease = true)
        val releaseList = listOf(botRelease, preRelease, v102, v101)
        val useCase = GetUnseenReleaseUseCase(
            releaseRepo = FakeReleaseRepo(lastSeenVersion = "1.0.1", releaseList = releaseList),
            currentVersion = AppVersion("1.0.2"),
        )
        val expected = listOf(v102)

        // when
        val emissions = useCase.invoke().toList()

        //then
        assertThat(emissions).isEqualTo(expected)
    }
}

private fun appRelease(version: String) = Release(
    version = version,
    isPreRelease = false,
    type = Release.Type.APP,
)

private class FakeReleaseRepo(
    private val lastSeenVersion: String?,
    private val releaseList: List<Release>,
) : ReleaseRepo {
    override suspend fun saveLastSeenVersion(version: String): EmptyResult<DataError.Local> = Result.Success(Unit)
    override fun getLastSeenVersion(): Flow<String?> = flowOf(lastSeenVersion)
    override fun getReleases(): Flow<List<Release>> = flowOf(releaseList)
}
