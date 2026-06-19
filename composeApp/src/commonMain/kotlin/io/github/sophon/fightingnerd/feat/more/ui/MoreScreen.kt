package io.github.sophon.fightingnerd.feat.more.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_donate
import fightingnerd.composeapp.generated.resources.more_donate_dialog_title
import fightingnerd.composeapp.generated.resources.more_theme_dialog_title
import io.github.sophon.fightingnerd.BuildKonfig
import io.github.sophon.fightingnerd.LocalBottomBarPadding
import io.github.sophon.fightingnerd.core.ui.SingleSelectDialog
import io.github.sophon.fightingnerd.feat.more.model.DonationMethod
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.ThemeMode
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MoreScreen(
    onNavigate: (MoreItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<MoreVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.navEvent.collect { onNavigate(it) }
    }

    Content(
        state = state,
        onItemClick = vm::onItemClick,
        onThemeItemClick = vm::onThemeDialog,
        onThemeSelected = vm::onThemeSelect,
        onDonateClick = vm::onDonateClick,
        onSelectDonationMethod = vm::onDonateItemClick,
        modifier = modifier,
    )
}

@Composable
private fun Content(
    state: MoreState,
    onItemClick: (MoreItem) -> Unit,
    onThemeItemClick: (isDialogVisible: Boolean) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onDonateClick: (isVisible: Boolean) -> Unit,
    onSelectDonationMethod: (DonationMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalBottomBarPadding.current),
        ) {
            ItemSection(
                items = state.items,
                onItemClick = onItemClick,
            )

            Footer(
                onDonateClick = { onDonateClick(true) }
            )
        }

        if (state.themeSelectorDialog.isVisible) {
            SingleSelectDialog(
                title = stringResource(Res.string.more_theme_dialog_title),
                items = state.themeSelectorDialog.themeModeLists,
                selectedItem = state.themeSelectorDialog.selectedTheme,
                onItemSelect = onThemeSelected,
                onDismiss = { onThemeItemClick(false) },
            )
        }

        if (state.donationSelectorDialog.isVisible) {
            SingleSelectDialog(
                title = stringResource(Res.string.more_donate_dialog_title),
                items = state.donationSelectorDialog.methodList,
                onItemSelect = onSelectDonationMethod,
                onDismiss = { onDonateClick(false) },
            )
        }
    }
}

@Composable
private fun ItemSection(
    items: List<MoreItem>,
    onItemClick: (MoreItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(nerdColorPalette.surface)
    ) {
        itemsIndexed(items) { index, moreItem ->
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(moreItem) }
                        .padding(nerdDimensions.componentPadding),
                ) {
                    Text(
                        text = stringResource(moreItem.stringResource),
                        style = nerdTypography.titleMedium,
                        color = nerdColorPalette.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = nerdColorPalette.textSecondary,
                    )
                }
                if (index != items.lastIndex) {
                    HorizontalDivider(
                        Modifier
                            .padding(horizontal = nerdDimensions.componentPadding)
                            .background(nerdColorPalette.dividerSubtle)
                    )
                }
            }
        }
    }
}

@Composable
private fun Footer(
    onDonateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = onDonateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = nerdColorPalette.accent,
                contentColor = nerdColorPalette.textPrimary,
            ),
            modifier = modifier,
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(nerdDimensions.iconDefault),
            )
            Spacer(Modifier.width(nerdDimensions.inlineGap))

            Text(
                text = stringResource(Res.string.more_donate),
                style = nerdTypography.labelLarge,
            )
        }
        Spacer(Modifier.height(nerdDimensions.componentPadding))

        Text(
            text = BuildKonfig.VERSION,
            style = nerdTypography.labelLarge,
            color = nerdColorPalette.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}


//region PREVIEW
@Composable
@Preview()
private fun SettingsPreview() {
    FightingNerdTheme {
        Content(
            state = MoreState(),
            onItemClick = {},
            onThemeItemClick = {},
            onDonateClick = {},
            onThemeSelected = {},
            onSelectDonationMethod = {},
        )
    }
}
//endregion
