package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.theme.ThemeMode

internal class SetThemeUseCase(
    private val repo: PreferenceRepo,
) {
    suspend fun invoke(themeMode: ThemeMode): EmptyResult<AppError> {
        val result = repo.setTheme(themeMode = themeMode)
        return result
    }
}
