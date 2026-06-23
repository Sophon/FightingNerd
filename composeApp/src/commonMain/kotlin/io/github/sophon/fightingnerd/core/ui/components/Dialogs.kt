package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun <T> SingleSelectDialog(
    title: String,
    items: List<T>,
    onItemSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    selectedItem: T? = null,
    itemLabel: (T) -> String = { it.toString() },
    background: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = background,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                items.forEach { item ->
                    if (selectedItem != null) {
                        RadioItem(
                            item = item,
                            itemLabel = itemLabel(item),
                            selectedItem = selectedItem,
                            onItemSelect = { onItemSelect(item) },
                        )
                    } else {
                        SurfaceItem(
                            item = item,
                            itemLabel = itemLabel(item),
                            onItemSelect = { onItemSelect(item) },
                            onDismiss = onDismiss,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T>RadioItem(
    item: T,
    itemLabel: String,
    selectedItem: T,
    onItemSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemSelect(item) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        RadioButton(
            selected = (item == selectedItem),
            onClick = { onItemSelect(item) }
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = itemLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun <T>SurfaceItem(
    item: T,
    itemLabel: String,
    onItemSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = {
            onItemSelect(item)
            onDismiss()
        },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = itemLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}


//region PREVIEW
enum class PreviewEnum(val string: String) {
    FirstItem("first"),
    SecondItem("second"),
    ThirdItem("third"),
}

@Composable
@Preview(showBackground = true)
private fun SingleSelectDialogPreview() {
    FightingNerdTheme {
        SingleSelectDialog(
            title = "Title",
            items = PreviewEnum.entries,
            selectedItem = PreviewEnum.SecondItem,
            onItemSelect = {},
            onDismiss = {},
            itemLabel = { it.string }
        )
    }
}
//endregion