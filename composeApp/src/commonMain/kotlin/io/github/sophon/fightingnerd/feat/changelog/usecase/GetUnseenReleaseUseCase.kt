package io.github.sophon.fightingnerd.feat.changelog.usecase

import io.github.sophon.fightingnerd.core.data.ReleaseRepo
import io.github.sophon.fightingnerd.core.model.AppVersion
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

internal class GetUnseenReleaseUseCase(
    private val releaseRepo: ReleaseRepo,
    private val currentVersion: AppVersion,
) {
    operator fun invoke(): Flow<Release> {
        return combine(
            releaseRepo.getLastSeenVersion(),
            releaseRepo.getReleases(),
        ) { lastSeenVersion, releaseList ->
            val filteredReleaseList = releaseList.releasedAppOnly()

            val newestUnseenRelease = when {
                (lastSeenVersion == null) -> filteredReleaseList.firstOrNull { it.version == currentVersion.value }
                lastSeenVersion.isNewest(filteredReleaseList) -> null
                else -> filteredReleaseList.firstOrNull()
            }
            newestUnseenRelease
        }.filterNotNull()
    }

    private fun List<Release>.releasedAppOnly(): List<Release> {
        val filtered = this
            .filter { it.isPreRelease.not() }
            .filter { it.type == Release.Type.APP }
        return filtered
    }

    private fun String.isNewest(releaseList: List<Release>): Boolean {
        val seenReleaseIndex = releaseList.indexOfFirst { it.version == this }
        if (seenReleaseIndex == -1) return false

        val isNewest = (seenReleaseIndex == 0)
        return isNewest
    }
}
