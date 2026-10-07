package io.github.sophon.fightingnerd.core.data

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

internal interface PreferenceRepo {
    fun subscribeToTheme(): Flow<ThemeMode>
    suspend fun setTheme(themeMode: ThemeMode): EmptyResult<AppError>

    fun subscribeToUpdateInterval(): Flow<Duration?>
    suspend fun setUpdateInterval(duration: Duration?): EmptyResult<AppError>
}
