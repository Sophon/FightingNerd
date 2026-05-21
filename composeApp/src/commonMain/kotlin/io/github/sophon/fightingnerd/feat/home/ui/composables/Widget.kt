package io.github.sophon.fightingnerd.feat.home.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.compose_multiplatform
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun Widget(
    widget: HomeViewState.WikiWidget,
    onExpandWidget: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(.2f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        WidgetHeader(
            game = widget.game,
            featureInfo = widget.featureInfo,
            isExpanded = widget.isExpanded,
            onExpandClick = onExpandWidget,
            isLoading = widget.isLoading,
        )

        if (widget.isExpanded && widget.isLoading.not()) {
            Spacer(Modifier.height(8.dp))

            CharacterList(
                characterList = widget.characterList,
                onCharacterClick = { /*TODO*/ },
            )
        }
    }
}

@Composable
internal fun WidgetHeader(
    game: Game,
    featureInfo: FeatureInfo,
    isExpanded: Boolean,
    onExpandClick: (Game) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp)
            .clickable(
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
        ) {
            AsyncImage(
                model = featureInfo.iconUrl,
                contentDescription = featureInfo.name,
                placeholder = painterResource(Res.drawable.compose_multiplatform),
                error = painterResource(Res.drawable.compose_multiplatform),
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Text(
                text = "${game.name} (${featureInfo.name})",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else {
            val icon = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}

@Composable
internal fun CharacterList(
    characterList: List<Character>,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = character.images?.iconUrl,
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
@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewDark() {
    AppTheme(darkTheme = true) {
        WidgetHeader(
            game = Game.Tekken8,
            featureInfo = FeatureInfo(
                name = "Wavu Wiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
                version = "1.0.0",
            ),
            isExpanded = true,
            onExpandClick = {},
            isLoading = false,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureInfoLoadingPreview() {
    AppTheme(darkTheme = true) {
        WidgetHeader(
            game = Game.Tekken8,
            featureInfo = FeatureInfo(
                name = "Wavu Wiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
                version = "1.0.0",
            ),
            isExpanded = true,
            onExpandClick = {},
            isLoading = true,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun FeatureInfoPreviewLight() {
    AppTheme(darkTheme = false) {
        WidgetHeader(
            game = Game.Tekken8,
            featureInfo = FeatureInfo(
                name = "WavuWiki",
                url = "https://wavu.wiki/t/Main_Page",
                iconUrl = "https://i.imgur.com/0cnTzNk.png",
                version = "1.0.0",
            ),
            isExpanded = false,
            onExpandClick = {},
            isLoading = false,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CharacterOverviewPreviewDark() {
    AppTheme(darkTheme = true) {
        CharacterList(
            characterList = mockCharacters(),
            onCharacterClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CharacterOverviewPreviewLight() {
    AppTheme(darkTheme = false) {
        CharacterList(
            characterList = mockCharacters(),
            onCharacterClick = {},
        )
    }
}

private fun mockCharacters(): List<Character> = listOf(
    Character(displayName = "Zuzana", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Eva", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Karolina", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Marcela", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Zdenka", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Hana", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
    Character(displayName = "Nina", aliasList = listOf(), wikiUrl = "", id = "", queryName = ""),
)

@Composable
@Preview(showBackground = true)
private fun WidgetExpandedPreviewDark() {
    AppTheme(darkTheme = true) {
        Widget(
            widget = mockWidget(isExpanded = true, isLoading = false),
            onExpandWidget = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun WidgetExpandedPreviewLight() {
    AppTheme(darkTheme = false) {
        Widget(
            widget = mockWidget(isExpanded = true, isLoading = false),
            onExpandWidget = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun WidgetCollapsedPreviewDark() {
    AppTheme(darkTheme = true) {
        Widget(
            widget = mockWidget(isExpanded = false, isLoading = false),
            onExpandWidget = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun WidgetLoadingPreviewDark() {
    AppTheme(darkTheme = true) {
        Widget(
            widget = mockWidget(isExpanded = false, isLoading = true),
            onExpandWidget = {},
        )
    }
}

private fun mockWidget(
    isExpanded: Boolean,
    isLoading: Boolean,
): HomeViewState.WikiWidget {
    val widget = HomeViewState.WikiWidget(
        game = Game.Tekken8,
        featureInfo = FeatureInfo(
            name = "Wavu Wiki",
            url = "https://wavu.wiki/t/Main_Page",
            iconUrl = "https://i.imgur.com/0cnTzNk.png",
            version = "1.0.0",
        ),
        characterList = mockCharacters(),
        isExpanded = isExpanded,
        isLoading = isLoading,
    )
    return widget
}
//endregion