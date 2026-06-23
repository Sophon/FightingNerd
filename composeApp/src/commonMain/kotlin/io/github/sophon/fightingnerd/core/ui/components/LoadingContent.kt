package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun LoadingContent(modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxSize()) {
        CircularLoader(Modifier.size(128.dp).align(Alignment.Center))
    }
}
