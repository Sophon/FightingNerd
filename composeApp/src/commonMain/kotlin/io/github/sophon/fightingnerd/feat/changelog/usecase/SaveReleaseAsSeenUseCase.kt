package io.github.sophon.fightingnerd.feat.changelog.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.mapError
import io.github.sophon.fightingnerd.core.data.ReleaseRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.model.AppVersion

@ExcludeFromCoverage("plain repo call")
internal class SaveReleaseAsSeenUseCase(
    private val releaseRepo: ReleaseRepo,
) {
    suspend operator fun invoke(version: String): EmptyResult<AppError> {
        return releaseRepo.saveLastSeenVersion(version)
            .mapError { AppError.IOError("SaveReleaseAsSeenUseCase") }
    }
}
