package com.example.cornerman.screens.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cornerman.theme.AppTheme
import com.example.wikiwavu.domain.model.Character
import cornerman.composeapp.generated.resources.Res
import cornerman.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val vm = koinViewModel<HomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        onCharacterClick = vm::onCharacterClick,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: HomeViewState,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) { paddingValues ->

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterHorizontally,
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            state.characterList.forEachIndexed { index, character ->
                CharacterPanel(
                    character = character,
                    onClick = { onCharacterClick(index) },
                )
            }
        }
    }
}

@Composable
private fun CharacterPanel(
    character: Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .width(100.dp)
            .height(128.dp)
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = character.portraitUrl,
            contentDescription = character.name,
            placeholder = painterResource(Res.drawable.compose_multiplatform),
            error = painterResource(Res.drawable.compose_multiplatform),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = character.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


//region PREVIEW
@Composable
@Preview(showBackground = true)
private fun HomeScreenPreviewDark() {
    AppTheme(darkTheme = true) {
        Content(
            state = HomeViewState.PREVIEW,
            onCharacterClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreviewLight() {
    AppTheme(darkTheme = false) {
        Content(
            state = HomeViewState.PREVIEW,
            onCharacterClick = {},
        )
    }
}
//endregion