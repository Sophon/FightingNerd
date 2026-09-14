package io.github.sophon.fightingnerd.feat.changelog

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import io.github.sophon.fightingnerd.feat.changelog.usecase.GetUnseenReleaseUseCase
import io.github.sophon.fightingnerd.feat.changelog.usecase.SaveReleaseAsSeenUseCase
import kotlinx.coroutines.flow.Flow

internal interface ChangelogClient {
    suspend fun saveReleaseAsSeen(version: String)
    fun subscribeToUnseenChangelog(): Flow<Release>
}


internal class ChangelogClientImpl(
    private val saveReleaseAsSeenUseCase: SaveReleaseAsSeenUseCase,
    private val getUnseenReleaseUseCase: GetUnseenReleaseUseCase,
): ChangelogClient {
    override suspend fun saveReleaseAsSeen(version: String) {
        saveReleaseAsSeenUseCase(version)
            .onError {
                Napier.e(tag = TAG) { it.errorMessage }
            }
    }

    override fun subscribeToUnseenChangelog(): Flow<Release> {
        return getUnseenReleaseUseCase()
    }


    private companion object {
        const val TAG = "ChangelogClientImpl"
    }
}
