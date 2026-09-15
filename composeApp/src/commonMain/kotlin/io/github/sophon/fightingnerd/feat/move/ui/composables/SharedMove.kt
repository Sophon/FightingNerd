package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_fighting_nerd
import fightingnerd.composeapp.generated.resources.move_list_field_damage
import fightingnerd.composeapp.generated.resources.move_list_field_guard
import fightingnerd.composeapp.generated.resources.move_list_field_on_block
import fightingnerd.composeapp.generated.resources.move_list_field_on_hit
import fightingnerd.composeapp.generated.resources.move_list_field_startup
import io.github.sophon.fightingnerd.core.ui.components.ImageCarousel
import io.github.sophon.fightingnerd.feat.move.model.Property
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.Field
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState.UiMove
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun SharedMove(
    uiMove: UiMove,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(nerdDimensions.cornerDefault))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(nerdDimensions.componentPadding)
    ) {
        Header(
            input = uiMove.input,
            name = uiMove.name,
            propertySet = uiMove.propertySet,
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(nerdDimensions.inlineGap),
            maxItemsInEachRow = COUNT_MAX_PER_ROW,
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentPaddingTight),
        ) {
            uiMove.coreFields.forEach { field ->
                FieldColumn(
                    field = field,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (isExpanded) {
            Details(uiMove)
        }
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Credits()
    }
}

@Composable
private fun Header(
    input: String,
    name: String?,
    propertySet: ImmutableSet<Property>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = input,
            style = nerdTypography.titleLarge,
            color = nerdColorPalette.textPrimary,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (name.isNullOrBlank().not()) {
                Text(
                    text = name,
                    style = nerdTypography.labelSmall,
                    color = nerdColorPalette.textSecondary,
                )
            }
            Properties(
                propertySet = propertySet
            )
        }
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = nerdDimensions.listRowPaddingVertical)
                .background(nerdColorPalette.dividerSubtle)
        )
    }
}

@Composable
private fun Details(
    uiMove: UiMove,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        when {
            uiMove.urls.hitboxImageList.isNotEmpty() -> {
                ImageCarousel(
                    imageList = uiMove.urls.hitboxImageList,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            }
            uiMove.urls.moveImageList.isNotEmpty() -> {
                MoveImage(uiMove.urls.moveImageList.first())
                Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
            }
        }

        FlowRow(
            maxItemsInEachRow = COUNT_MAX_PER_ROW,
            verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentPaddingTight),
            modifier = Modifier
                .fillMaxWidth()
                .padding(nerdDimensions.inlineGapTight),
        ) {
            uiMove.optionalFields.forEach { field ->
                FieldColumn(
                    field = field,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (uiMove.optionalFields.isNotEmpty()) {
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }

        uiMove.notes.takeIf { it.isNotEmpty() }?.let { noteList ->
            NotesSection(noteList)
            Spacer(Modifier.height(nerdDimensions.componentPaddingTight))
        }
    }
}

@Composable
private fun Credits(
    modifier: Modifier = Modifier
) {
   Row(
       horizontalArrangement = Arrangement.End,
       verticalAlignment = Alignment.Bottom,
       modifier = modifier.fillMaxWidth(),
   ) {
       Text(
           text = "Fighting Nerd",
           style = nerdTypography.labelMedium,
           color = nerdColorPalette.textTertiary,
       )
       Spacer(Modifier.width(nerdDimensions.inlineGapTight))

       Image(
           painter = painterResource(Res.drawable.ic_fighting_nerd),
           contentDescription = null,
           modifier = Modifier.size(nerdDimensions.iconInline),
       )
   }
}


private const val COUNT_MAX_PER_ROW = 3


//region PREVIEW
private val previewMove = UiMove(
    id = "nina-hub1",
    input = "H.ub1",
    name = "Neck Hunter: Villain",
    propertySet = persistentSetOf(Property.Heat, Property.Homing),
    coreFields = persistentListOf(
        Field(Res.string.move_list_field_startup, "i24"),
        Field(Res.string.move_list_field_guard, "h"),
        Field(Res.string.move_list_field_damage, "25"),
        Field(Res.string.move_list_field_on_block, "+8"),
        Field(Res.string.move_list_field_on_hit, "+60a"),
    ),
    optionalFields = persistentListOf(),
    urls = UiMove.Urls(
        hitboxImageList = persistentListOf("a", "b"),
    ),
    notes = persistentListOf(
        "Strong Aerial Tailspin",
        "Homing",
        "Consumes 150F of remaining Heat time",
    ),
)

@Preview
@Composable
private fun CollapsedSharedMovePreview() {
    FightingNerdTheme {
        SharedMove(
            uiMove = previewMove,
            isExpanded = false,
            modifier = Modifier.wrapContentHeight(unbounded = true),
        )
    }
}

@Preview
@Composable
private fun ExpandedSharedMovePreview() {
    FightingNerdTheme {
        SharedMove(
            uiMove = previewMove,
            isExpanded = true,
            modifier = Modifier.wrapContentHeight(unbounded = true),
        )
    }
}
//endregion
