package io.github.sophon.fightingnerd.feat.changelog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ChangelogDialog(
    release: Release,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = release.version,
                style = nerdTypography.headlineSmall,
                color = nerdColorPalette.textPrimary,
            )
        },
        text = {
            Column {
                release.changeList.forEach { change ->
                    Text(
                        text = "• $change",
                        style = nerdTypography.bodyLarge,
                        color = nerdColorPalette.textPrimary,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    style = nerdTypography.labelLarge,
                    color = nerdColorPalette.accent,
                )
            }
        },
        modifier = modifier,
    )
}


//region PREVIEW
@Composable
@Preview()
private fun ChangelogDialogPreview() {
    FightingNerdTheme {
        ChangelogDialog(
            release = Release(
                version = "v4.0.1",
                isPreRelease = false,
                changeList = persistentListOf(
                    "fixed updater on iOS",
                    "improved character search performance",
                    "added Tekken 8 tier list",
                ),
                type = Release.Type.APP,
            ),
            onDismiss = {},
        )
    }
}
//endregion
