package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

internal interface PreferenceRepo {
    fun subscribeToTheme(): Flow<ThemeMode>
    suspend fun setTheme(themeMode: ThemeMode): EmptyResult<AppError>
}
