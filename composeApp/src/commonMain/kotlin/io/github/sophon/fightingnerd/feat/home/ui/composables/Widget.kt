package io.github.sophon.fightingnerd.feat.home.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.feature.Game
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun WidgetSection(
    widgetList: List<GameWidget>,
    onExpandWidget: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    val botPaddingValues = PaddingValues(bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Top,
        ),
        contentPadding = botPaddingValues,
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        widgetList.forEach { widget ->
            item(key = "header_${widget.game.id}") {
                WidgetHeader(
                    game = widget.game,
                    featureName = widget.featureName,
                    isExpanded = widget.isExpanded,
                    onExpandClick = onExpandWidget,
                    isLoading = widget.isLoading,
                )
            }

            if (widget.isExpanded) {
                val rows = widget.characterList.chunked(4) //TODO: calculate columns
                items(
                    items = rows,
                    key = { row -> "row_${widget.game.id}_${row.first().id}" },
                ) { rowCharacters ->
                    CharacterRow(
                        characterList = rowCharacters,
                        onCharacterClick = {},
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun WidgetHeader(
    game: Game,
    featureName: String,
    isExpanded: Boolean,
    onExpandClick: (Game) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onExpandClick(game) },
                enabled = isLoading.not(),
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start,
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            AsyncImage(
                model = game.iconUrl,
                contentDescription = featureName,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Text(
                text = game.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        } else {
            val icon = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
internal fun CharacterRow(
    characterList: List<GameWidget.Character>,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        characterList.forEach { character ->
            CharacterPanel(
                character = character,
                onClick = { onCharacterClick(character.queryName) },
            )
        }
    }
}

@Composable
private fun CharacterPanel(
    character: GameWidget.Character,
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
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = character.iconUrl,
            contentDescription = character.displayName,
            placeholder = painterResource(Res.drawable.compose_multiplatform),
            error = painterResource(Res.drawable.compose_multiplatform),
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = character.displayName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}


//region PREVIEW
@Preview
@Composable
private fun WidgetSectionDarkPreview() {
    AppTheme(darkTheme = true) {
        WidgetSection(
            widgetList = mockWidgetList(),
            onExpandWidget = {},
        )
    }
}

@Preview
@Composable
private fun WidgetSectionLightPreview() {
    AppTheme(darkTheme = false) {
        WidgetSection(
            widgetList = mockWidgetList(),
            onExpandWidget = {},
        )
    }
}

private fun mockCharacters(): List<GameWidget.Character> {
    val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
    return names.mapIndexed { index, name ->
        GameWidget.Character(
            id = "char_$index",
            displayName = name,
            queryName = "",
        )
    }
}

private fun mockWidget(
    game: Game,
    featureName: String,
    isExpanded: Boolean,
    isLoading: Boolean,
): GameWidget {
    val widget = GameWidget(
        game = game,
        featureName = featureName,
        characterList = mockCharacters(),
        isExpanded = isExpanded,
        isLoading = isLoading,
    )
    return widget
}

private fun mockWidgetList(): List<GameWidget> {
    return listOf(
        mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true, isLoading = false),
        mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false, isLoading = false),
        mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false, isLoading = false),
    )
}
//endregion