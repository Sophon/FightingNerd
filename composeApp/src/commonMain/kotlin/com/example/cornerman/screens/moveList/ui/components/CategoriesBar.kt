package com.example.cornerman.screens.moveList.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.cornerman.screens.moveList.model.MoveCategory
import com.example.cornerman.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoriesBar(
    categories: List<MoveCategory>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(8.dp))
            .border(
                width = .5.dp,
                color = MaterialTheme.colorScheme.outline.copy(.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(categories) { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable { onCategoryClick(category.name) },
            )
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
            onCategoryClick = {},
        )
    }
}
//endregion