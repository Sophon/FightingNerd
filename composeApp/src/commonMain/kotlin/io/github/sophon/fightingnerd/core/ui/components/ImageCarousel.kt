package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ImageCarousel(
    imageList: ImmutableList<String>,
    modifier: Modifier = Modifier
) {
    if (imageList.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageList.size })

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(nerdColorPalette.surface),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            SubcomposeAsyncImage(
                model = imageList[page],
                contentDescription = null,
                loading = { CircularLoader() },
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
            )
        }

        if (imageList.size > 1) {
            PagerIndicator(
                pageCount = imageList.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = nerdDimensions.componentPaddingTight),
            )
        }
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(nerdDimensions.inlineGapTight),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(CircleShape)
            .background(nerdColorPalette.scrim.copy(alpha = 0.4f))
            .padding(
                horizontal = nerdDimensions.chipPaddingHorizontal,
                vertical = nerdDimensions.chipPaddingVertical,
            ),
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val color = if (isActive) {
                nerdColorPalette.accent
            } else {
                nerdColorPalette.textDisabled
            }
            Box(
                modifier = Modifier
                    .size(if (isActive) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}


//region PREVIEW
@Preview
@Composable
private fun ImageCarouselPreview() {
    FightingNerdTheme {
        Surface {
            ImageCarousel(
                imageList = persistentListOf(
                    "https://example.com/hitbox-1.png",
                    "https://example.com/hitbox-2.png",
                    "https://example.com/hitbox-3.png",
                ),
                modifier = Modifier.padding(nerdDimensions.componentPadding),
            )
        }
    }
}
//endregion