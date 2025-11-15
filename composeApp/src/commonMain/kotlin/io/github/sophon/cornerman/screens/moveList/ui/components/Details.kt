package io.github.sophon.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ExpandLess
import androidx.compose.material.icons.twotone.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.sophon.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Details(
    title: String,
    items: List<String>,
    isExpanded: Boolean? = null,
    onExpandClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandClick)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = title.uppercase(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )

            if (isExpanded != null) {
                Icon(
                    imageVector = if (isExpanded) {
                        Icons.TwoTone.ExpandLess
                    } else {
                        Icons.TwoTone.ExpandMore
                    },
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(2.dp))

        if (isExpanded != false) {
            items.forEach { note ->
                Text(
                    text = "• $note",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(1.dp))
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun DetailsPreviewDark() {
    AppTheme(darkTheme = true) {
        Details(
            title = "⭐️ Drive & Super",
            onExpandClick = {},
            items = listOf(
                "DR (OH | OB): +7 | +2",
                "DRc (OH | OB): +21 | +8",
                "Drive damage (OH | OB): [8000] | 5000",
                "Drive gain: 2000",
                "SUP gain (OH | OB): 1000 (700) | 500 (250)"
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun DetailsPreviewLight() {
    AppTheme(darkTheme = false) {
        Details(
            title = "⭐️ Drive & Super",
            onExpandClick = {},
            items = listOf(
                "DR (OH | OB): +7 | +2",
                "DRc (OH | OB): +21 | +8",
                "Drive damage (OH | OB): [8000] | 5000",
                "Drive gain: 2000",
                "SUP gain (OH | OB): 1000 (700) | 500 (250)"
            )
        )
    }
}
//endregion