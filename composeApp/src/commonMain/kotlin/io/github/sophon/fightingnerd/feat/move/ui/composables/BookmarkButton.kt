package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Toc
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.sophon.fightingnerd.feat.move.model.Bookmark
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

private val BOOKMARK_LIST_MAX_HEIGHT = 240.dp

@Composable
internal fun BookmarksButton(
    bookmarks: MoveListState.Bookmarks,
    onBookmarkSwitch: () -> Unit,
    onBookmarkClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
            .padding(horizontal = nerdDimensions.componentGap),
    ) {
        AnimatedVisibility(
            visible = bookmarks.isExpanded,
            enter = expandVertically(expandFrom = Alignment.Bottom),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom),
        ) {
            BookmarkList(
                bookmarkList = bookmarks.bookmarkList,
                onBookmarkClick = onBookmarkClick,
            )
        }

        val shape = if (bookmarks.isExpanded) {
            RoundedCornerShape(
                bottomStart = nerdDimensions.cornerDefault,
                bottomEnd = nerdDimensions.cornerDefault,
            )
        } else {
            RoundedCornerShape(nerdDimensions.cornerDefault)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(nerdDimensions.iconHeadline)
                .clip(shape)
                .background(color = nerdColorPalette.surface)
                .border(
                    width = nerdDimensions.strokeThin,
                    color = nerdColorPalette.dividerSubtle,
                    shape = shape,
                )
                .clickable(onClick = onBookmarkSwitch),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Toc,
                contentDescription = null,
                tint = nerdColorPalette.textPrimary,
            )
        }
    }
}

@Composable
private fun BookmarkList(
    bookmarkList: ImmutableList<Bookmark>,
    onBookmarkClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(
        topStart = nerdDimensions.cornerDefault,
        topEnd = nerdDimensions.cornerDefault,
        bottomStart = nerdDimensions.cornerDefault,
    )
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .heightIn(max = BOOKMARK_LIST_MAX_HEIGHT)
            .clip(shape)
            .background(nerdColorPalette.surface)
            .border(
                width = nerdDimensions.strokeThin,
                color = nerdColorPalette.dividerSubtle,
                shape = shape,
            )
            .verticalScroll(rememberScrollState())
            .padding(vertical = nerdDimensions.listRowPaddingVertical)
    ) {
        bookmarkList.forEach { bookmark ->
            Text(
                text = "${bookmark.id} •",
                style = nerdTypography.bodyLarge,
                color = nerdColorPalette.textPrimary,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBookmarkClick(bookmark.moveListIndex) }
                    .padding(
                        horizontal = nerdDimensions.listRowPaddingHorizontal,
                        vertical = nerdDimensions.listRowPaddingVertical,
                    ),
            )
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun BookmarkPreviewExpanded() {
    FightingNerdTheme {
        BookmarksButton(
            bookmarks = MoveListState.Bookmarks(
                isExpanded = true,
                bookmarkList = persistentListOf(
                    Bookmark(id = "Heat", moveListIndex = 0),
                    Bookmark(id = "n", moveListIndex = 0),
                    Bookmark(id = "f", moveListIndex = 0),
                    Bookmark(id = "df", moveListIndex = 0),
                    Bookmark(id = "d", moveListIndex = 0),
                    Bookmark(id = "db", moveListIndex = 0),
                    Bookmark(id = "b", moveListIndex = 0),
                    Bookmark(id = "u", moveListIndex = 0),
                    Bookmark(id = "ub", moveListIndex = 0),
                    Bookmark(id = "Motion Input", moveListIndex = 0),
                )
            ),
            onBookmarkSwitch = {},
            onBookmarkClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun BookmarkPreviewCollapsed() {
    FightingNerdTheme {
        BookmarksButton(
            bookmarks = MoveListState.Bookmarks(
                isExpanded = false,
                bookmarkList = persistentListOf(),
            ),
            onBookmarkSwitch = {},
            onBookmarkClick = {},
        )
    }
}
//endregion