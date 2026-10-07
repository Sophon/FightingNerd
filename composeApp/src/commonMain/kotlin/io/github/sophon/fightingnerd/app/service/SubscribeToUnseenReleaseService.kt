package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.app.model.AppVersion
import io.github.sophon.fightingnerd.app.model.Release
import io.github.sophon.fightingnerd.app.outPort.LastSeenReleasePort
import io.github.sophon.fightingnerd.app.outPort.ReleasePort
import io.github.sophon.fightingnerd.inPort.SubscribeToUnseenReleaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

internal class SubscribeToUnseenReleaseService(
    private val releasePort: ReleasePort,
    private val lastSeenReleasePort: LastSeenReleasePort,
    private val currentVersion: AppVersion,
): SubscribeToUnseenReleaseUseCase {
    override fun invoke(): Flow<Release> {
        val releaseListFlow = flow {
            releasePort.getReleases()
                .onSuccess { releaseList -> emit(releaseList) }
        }
        return combine(
            lastSeenReleasePort.subscribeToLastSeenVersion(),
            releaseListFlow,
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
