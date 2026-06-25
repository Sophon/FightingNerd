package io.github.sophon.fightingnerd.navigation.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.bottom_bar_item_character_list
import fightingnerd.composeapp.generated.resources.bottom_bar_item_more
import fightingnerd.composeapp.generated.resources.bottom_bar_item_quiz
import io.github.sophon.fightingnerd.core.ui.FlexibleIcon
import io.github.sophon.fightingnerd.navigation.domain.BottomBarItem
import io.github.sophon.fightingnerd.navigation.domain.Destination
import io.github.sophon.fightingnerd.navigation.domain.rootDestinations
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

internal val bottomBarItems: List<BottomBarItem> = rootDestinations.map { destination ->
    when (destination) {
        Destination.Home -> BottomBarItem(
            label = Res.string.bottom_bar_item_character_list,
            icon = FlexibleIcon.Vector(Icons.Default.GridView),
            destination = destination,
        )
        Destination.QuizOverview -> BottomBarItem(
            label = Res.string.bottom_bar_item_quiz,
            icon = FlexibleIcon.Vector(Icons.Default.Quiz),
            destination = destination,
        )
        Destination.More -> BottomBarItem(
            label = Res.string.bottom_bar_item_more,
            icon = FlexibleIcon.Vector(Icons.Outlined.MoreHoriz),
            destination = destination,
        )
        else -> error("topLevelOrder contains $destination but BottomNavBarView has no mapping for it")
    }
}


@Composable
internal fun BottomNavBarView(
    currentRoot: Destination,
    onTabClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
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
                    color = nerdColorPalette.surfaceHigh,
                    shape = RoundedCornerShape(RADIUS_CORNER.dp),
                )
                .padding(2.dp),
        ) {
            bottomBarItems.forEach { item ->
                BarItem(
                    item = item,
                    isSelected = (item.destination == currentRoot),
                    onClick = { onTabClick(item.destination) },
                    modifier = Modifier.widthIn(min = 80.dp),
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
        itemColor = nerdColorPalette.accent
        backgroundColor = nerdColorPalette.surface
    } else {
        itemColor = nerdColorPalette.textPrimary
        backgroundColor = Color.Transparent
    }
    val shape = RoundedCornerShape(RADIUS_CORNER.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .clickable(enabled = true, onClick = onClick)
            .background(
                color = backgroundColor,
                shape = shape,
            )
            .padding(nerdDimensions.componentGapTight),
    ) {
        when (val icon = item.icon) {
            is FlexibleIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(nerdDimensions.iconDefault),
                    tint = itemColor,
                )
            }
            is FlexibleIcon.Resource -> {
                Icon(
                    painter = painterResource(icon.drawableResource),
                    contentDescription = null,
                    modifier = Modifier.size(nerdDimensions.iconDefault),
                    tint = itemColor,
                )
            }
        }

        Text(
            text = stringResource(item.label),
            style = nerdTypography.labelMedium,
            color = itemColor,
        )
    }
}

private const val RADIUS_CORNER = 30


//region PREVIEW
@Composable
@Preview(showBackground = false)
private fun BottomBarPreview() {
    FightingNerdTheme {
        BottomNavBarView(
            currentRoot = Destination.Home,
            onTabClick = {},
        )
    }
}
//endregion
