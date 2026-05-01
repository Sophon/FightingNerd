package io.github.sophon.fightingnerd.uiGallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun CharacterOverview(
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
private fun CharacterOverviewPreviewDark() {
    AppTheme(darkTheme = true) {
        CharacterOverview(
            characterList = mockCharacters(),
            onCharacterClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CharacterOverviewPreviewLight() {
    AppTheme(darkTheme = false) {
        CharacterOverview(
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
//endregion