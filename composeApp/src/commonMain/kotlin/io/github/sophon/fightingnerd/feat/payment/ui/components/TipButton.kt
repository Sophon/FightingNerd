package io.github.sophon.fightingnerd.feat.payment.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.more_donate
import io.github.sophon.fightingnerd.feat.payment.ui.TipVM
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TipButton(modifier: Modifier = Modifier) {
    val vm = koinViewModel<TipVM>()
    val state by vm.state.collectAsStateWithLifecycle()

    Button(
        onClick = vm::onTipButtonClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = nerdColorPalette.accent,
            contentColor = nerdColorPalette.textPrimary,
        ),
        shape = RoundedCornerShape(nerdDimensions.cornerDefault),
        contentPadding = PaddingValues(
            horizontal = nerdDimensions.buttonPaddingHorizontal,
            vertical = nerdDimensions.buttonPaddingVertical,
        ),
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(nerdDimensions.iconInline),
        )
        Spacer(Modifier.width(nerdDimensions.inlineGap))
        Text(
            text = stringResource(Res.string.more_donate),
            style = nerdTypography.labelLarge,
        )
    }

    if (state.isDialogVisible) {
        TipDialog(
            onOptionSelected = vm::onTipOptionSelected,
            onRetry = vm::onRetryLoad,
            onDismiss = vm::onDismissDialog,
        )
    }
}
