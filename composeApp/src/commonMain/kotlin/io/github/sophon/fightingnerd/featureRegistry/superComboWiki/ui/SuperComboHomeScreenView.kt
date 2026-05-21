package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.feat.home.ui.composables.CharacterList
import io.github.sophon.fightingnerd.feat.home.ui.composables.WidgetHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SuperComboHomeScreenView(
    featureInfo: FeatureInfo,
    onCharacterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<SuperComboHomeVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Content(
        state = state,
        featureInfo = featureInfo,
        onCharacterClick = onCharacterClick,
        onExpandClick = vm::onExpandClick,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: SuperComboHomeScreenViewState,
    onCharacterClick: (String) -> Unit,
    onExpandClick: () -> Unit,
    featureInfo: FeatureInfo? = null,
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
//        if (featureInfo != null) {
//            WidgetHeader(
//                featureInfo = featureInfo,
//                isExpanded = state.isExpanded,
//                onExpandClick = onExpandClick,
//                isLoading = state.isLoading,
//            )
//        }

        if (state.isExpanded && state.isLoading.not()) {
            Spacer(Modifier.height(8.dp))

            CharacterList(
                characterList = state.characterList,
                onCharacterClick = onCharacterClick,
            )
        }
    }
}