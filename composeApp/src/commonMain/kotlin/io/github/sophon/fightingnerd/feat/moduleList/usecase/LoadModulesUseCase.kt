package io.github.sophon.fightingnerd.feat.moduleList.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.moduleList.model.WikiModule

internal class LoadModulesUseCase(
    //loader
) {
    suspend fun invoke(): Result<List<WikiModule>, AppError> {
        TODO("implement")
    }
}