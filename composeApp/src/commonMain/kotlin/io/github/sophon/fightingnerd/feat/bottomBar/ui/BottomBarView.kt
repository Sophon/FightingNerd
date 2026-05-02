package io.github.sophon.fightingnerd.feat.bottomBar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.fightingnerd.feat.bottomBar.model.BottomBarItem
import io.github.sophon.fightingnerd.feat.core.FlexibleIcon
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun BottomBarView(
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<BottomBarVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    BottomBarContent(
        state = state,
        onItemClick = vm::onItemClick,
        modifier = modifier,
    )
}

@Composable
private fun BottomBarContent(
    state: BottomBarState,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.Transparent),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(48),
                )
                .padding(8.dp)
        ) {
            state.itemList.forEachIndexed { index, item ->
                BarItem(
                    item = item,
                    isSelected = (index == state.selectedItemIndex),
                    onClick = { onItemClick(index) },
                    modifier = Modifier.widthIn(min = 96.dp)
                )
            }
        }
    }
}

@Composable
private fun BarItem(
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemColor: Color
    val backgroundColor: Color

    if (isSelected) {
        itemColor = MaterialTheme.colorScheme.onSecondaryContainer
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    } else {
        itemColor = MaterialTheme.colorScheme.onSurfaceVariant
        backgroundColor = Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(48))
            .clickable(enabled = true, onClick = onClick)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(48),
            )
            .padding(8.dp),
    ) {
        when (val icon = item.icon) {
            is FlexibleIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = itemColor,
                )
            }
            is FlexibleIcon.Resource -> {
                Icon(
                    painter = painterResource(icon.drawableResource),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = itemColor,
                )
            }
        }

        Text(
            text = stringResource(item.label),
            style = MaterialTheme.typography.labelSmall,
            color = itemColor,
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = false)
private fun LightPreview() {
    AppTheme(darkTheme = false) {
        BottomBarContent(
            state = BottomBarState(),
            onItemClick = {},
        )
    }
}

@Composable
@Preview(showBackground = false)
private fun DarkPreview() {
    AppTheme(darkTheme = true) {
        BottomBarContent(
            state = BottomBarState(),
            onItemClick = {},
        )
    }
}
//endregion