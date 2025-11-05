package io.github.sophon.cornerman.screens.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    val vm = koinInject<SettingsVM>()
    val state by vm.state.collectAsStateWithLifecycle()
}