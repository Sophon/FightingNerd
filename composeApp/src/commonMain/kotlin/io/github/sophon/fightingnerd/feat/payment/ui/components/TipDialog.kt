package io.github.sophon.fightingnerd.feat.payment.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.payment_tip_dialog_description
import fightingnerd.composeapp.generated.resources.payment_tip_dialog_title
import fightingnerd.composeapp.generated.resources.payment_tip_error_load
import fightingnerd.composeapp.generated.resources.payment_tip_retry
import io.github.sophon.fightingnerd.feat.payment.model.TipOption
import io.github.sophon.fightingnerd.feat.payment.ui.TipVM
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TipDialog(
    onOptionSelected: (TipOption) -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<TipVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(nerdDimensions.cornerExtra),
            color = nerdColorPalette.surfaceHigh,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(vertical = nerdDimensions.componentPadding),
            ) {
                Text(
                    text = stringResource(Res.string.payment_tip_dialog_title).uppercase(),
                    style = nerdTypography.headlineSmall,
                    color = nerdColorPalette.textPrimary,
                    modifier = Modifier.padding(horizontal = nerdDimensions.dialogPadding),
                )
                Spacer(Modifier.padding(vertical = nerdDimensions.componentGapTight))

                Text(
                    text = stringResource(Res.string.payment_tip_dialog_description),
                    style = nerdTypography.bodyMedium,
                    color = nerdColorPalette.textSecondary,
                    modifier = Modifier.padding(horizontal = nerdDimensions.dialogPadding),
                )
                Spacer(Modifier.padding(vertical = nerdDimensions.componentGapTight))

                when {
                    state.isLoading -> LoadingRow()
                    state.hasLoadError -> ErrorRow(onRetry = onRetry)
                    else -> {
                        state.tipOptionList.forEach { option ->
                            TipRow(
                                title = option.title,
                                formattedPrice = option.formattedPrice,
                                onClick = {
                                    onOptionSelected(option)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingRow() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = nerdDimensions.sectionGap),
    ) {
        CircularProgressIndicator(color = nerdColorPalette.accent)
    }
}

@Composable
private fun ErrorRow(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGapTight),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = nerdDimensions.dialogPadding,
                vertical = nerdDimensions.componentPadding,
            ),
    ) {
        Text(
            text = stringResource(Res.string.payment_tip_error_load),
            style = nerdTypography.bodyLarge,
            color = nerdColorPalette.textSecondary,
            textAlign = TextAlign.Center,
        )
        TextButton(
            onClick = onRetry,
            colors = ButtonDefaults.textButtonColors(contentColor = nerdColorPalette.accent),
        ) {
            Text(
                text = stringResource(Res.string.payment_tip_retry),
                style = nerdTypography.labelLarge,
            )
        }
    }
}

@Composable
private fun TipRow(
    title: String,
    formattedPrice: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(nerdDimensions.cornerDefault),
        color = nerdColorPalette.surface,
        border = BorderStroke(nerdDimensions.strokeThin, nerdColorPalette.dividerSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = nerdDimensions.componentPadding,
                vertical = nerdDimensions.inlineGapTight,
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = nerdTypography.titleMedium,
                color = nerdColorPalette.textPrimary,
                modifier = Modifier
                    .padding(
                        horizontal = nerdDimensions.componentPadding,
                        vertical = nerdDimensions.componentPaddingTight,
                    )
                    .weight(1f),
            )

            Text(
                text = formattedPrice,
                style = nerdTypography.titleMedium,
                color = nerdColorPalette.textPrimary,
                modifier = Modifier.padding(
                    horizontal = nerdDimensions.componentPadding,
                    vertical = nerdDimensions.componentPaddingTight,
                ),
            )
        }
    }
}
