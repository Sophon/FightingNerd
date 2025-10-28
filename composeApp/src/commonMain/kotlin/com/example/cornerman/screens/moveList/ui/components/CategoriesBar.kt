package com.example.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cornerman.screens.moveList.domain.MoveCategory
import com.example.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoriesBar(
    categories: List<MoveCategory>,
    listState: LazyListState,
    currentCategoryIndex: Int,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxHeight(.75f)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
            .border(
                width = .5.dp,
                color = MaterialTheme.colorScheme.outline.copy(.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            val isSelected = (index == currentCategoryIndex)
            val color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else Color.Transparent

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(IntrinsicSize.Min),
            ) {
                VerticalDivider(
                    color = color,
                    thickness = 4.dp,
                    modifier = Modifier.fillMaxHeight(),
                )

                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable { onCategoryClick(index) },
                )
            }
        }
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun CategoryBarPreviewLight() {
    AppTheme(darkTheme = false) {
        CategoriesBar(
            categories = listOf(
                MoveCategory("d", listOf()),
                MoveCategory("b", listOf()),
                MoveCategory("db", listOf()),
                MoveCategory("df", listOf()),
                MoveCategory("JGS", listOf()),
                MoveCategory("BT (back turned)", listOf()),
                MoveCategory("Throws", listOf()),
            ),
            listState = rememberLazyListState(),
            currentCategoryIndex = 0,
            onCategoryClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CategoryBarPreviewDark() {
    AppTheme(darkTheme = true) {
        CategoriesBar(
            categories = listOf(
                MoveCategory("d", listOf()),
                MoveCategory("b", listOf()),
                MoveCategory("db", listOf()),
                MoveCategory("df", listOf()),
                MoveCategory("JGS", listOf()),
                MoveCategory("BT (back turned)", listOf()),
                MoveCategory("Throws", listOf()),
            ),
            listState = rememberLazyListState(),
            currentCategoryIndex = 3,
            onCategoryClick = {},
        )
    }
}
//endregion