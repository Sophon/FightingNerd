package io.github.sophon.fightingnerd.feat.more.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_theme_dialog_title
import io.github.sophon.fightingnerd.feat.more.model.Theme
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ThemeDialog(
    themeList: List<Theme>,
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = modifier,
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = stringResource(Res.string.more_theme_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )

                themeList.forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = { onThemeSelected(theme) },
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(theme.stringResource),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        ThemeDialog(
            themeList = Theme.entries,
            selectedTheme = Theme.System,
            onThemeSelected = {},
            onDismiss = {},
        )
    }
}
//endregion