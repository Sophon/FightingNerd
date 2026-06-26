package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

internal class SubscribeToThemeUseCase(
    private val repo: PreferenceRepo,
) {
    fun invoke(): Flow<ThemeMode> {
        val flow = repo.subscribeToTheme()
        return flow
    }
}
