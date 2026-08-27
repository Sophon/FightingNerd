package io.github.sophon.fightingnerd.feat.move.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.move_list_char_air_accel
import fightingnerd.composeapp.generated.resources.move_list_char_air_bwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_air_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_air_speed
import fightingnerd.composeapp.generated.resources.move_list_char_backdash
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_bwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_dash_accel
import fightingnerd.composeapp.generated.resources.move_list_char_dash_frames
import fightingnerd.composeapp.generated.resources.move_list_char_dash_init_spd
import fightingnerd.composeapp.generated.resources.move_list_char_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_dmg_received_mod
import fightingnerd.composeapp.generated.resources.move_list_char_double_jump_height
import fightingnerd.composeapp.generated.resources.move_list_char_drush_max
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_block
import fightingnerd.composeapp.generated.resources.move_list_char_drush_min_throw
import fightingnerd.composeapp.generated.resources.move_list_char_full_hop_height
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_dist
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_dash_speed
import fightingnerd.composeapp.generated.resources.move_list_char_fwd_walk_speed
import fightingnerd.composeapp.generated.resources.move_list_char_ground_friction
import fightingnerd.composeapp.generated.resources.move_list_char_guts
import fightingnerd.composeapp.generated.resources.move_list_char_h_jump_speed
import fightingnerd.composeapp.generated.resources.move_list_char_hitstun_gravity
import fightingnerd.composeapp.generated.resources.move_list_char_hp
import fightingnerd.composeapp.generated.resources.move_list_char_hp_health
import fightingnerd.composeapp.generated.resources.move_list_char_hp_life_points
import fightingnerd.composeapp.generated.resources.move_list_char_hp_mod
import fightingnerd.composeapp.generated.resources.move_list_char_jump_startup
import fightingnerd.composeapp.generated.resources.move_list_char_ki_mod
import fightingnerd.composeapp.generated.resources.move_list_char_max_fall_speed
import fightingnerd.composeapp.generated.resources.move_list_char_max_run_speed
import fightingnerd.composeapp.generated.resources.move_list_char_prejump
import fightingnerd.composeapp.generated.resources.move_list_char_short_hop_height
import fightingnerd.composeapp.generated.resources.move_list_char_throw_dmg
import fightingnerd.composeapp.generated.resources.move_list_char_umo
import fightingnerd.composeapp.generated.resources.move_list_char_weight
import io.github.sophon.fightingnerd.feat.move.ui.MoveListState
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import io.github.sophon.wikiSuperCombo.integration.model.MK1Properties
import io.github.sophon.wikiSuperCombo.integration.model.SF6Properties
import io.github.sophon.wikidragdown.integration.model.Roa2Properties
import io.github.sophon.wikidustloop.integration.model.BBProperties
import io.github.sophon.wikidustloop.integration.model.DBFZProperties
import io.github.sophon.wikidustloop.integration.model.GBVSRProperties
import io.github.sophon.wikidustloop.integration.model.GGSTProperties
import io.github.sophon.wikidustloop.integration.model.MTFSProperties
import io.github.sophon.wikimizuumi.integration.model.Uni2Properties
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val PANEL_MAX_HEIGHT = 400.dp

@Immutable
private data class InfoCellData(
    val label: StringResource,
    val value: String?,
)

@Composable
internal fun CharacterInfoBox(
    character: MoveListState.MoveListCharacter,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(nerdDimensions.cornerDefault)
    Column(
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(nerdColorPalette.background)
            .border(
                width = nerdDimensions.strokeThin,
                color = nerdColorPalette.dividerSubtle,
                shape = shape,
            )
            .heightIn(max = PANEL_MAX_HEIGHT)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = nerdDimensions.componentPadding,
                vertical = nerdDimensions.componentPadding,
            ),
    ) {
        when (character.characterProperties) {
            is SF6Properties -> Sf6Rows(hp = character.hp, props = character.characterProperties)
            is MK1Properties -> Mk1Rows(hp = character.hp, props = character.characterProperties)
            is GGSTProperties -> GgstRows(umo = character.umo, props = character.characterProperties)
            is BBProperties -> BbcfRows(hp = character.hp, umo = character.umo, props = character.characterProperties)
            is DBFZProperties -> DbfzRows(hp = character.hp, props = character.characterProperties)
            is GBVSRProperties -> GbvsrRows(hp = character.hp, umo = character.umo, props = character.characterProperties)
            is MTFSProperties -> MtfsRows(umo = character.umo, props = character.characterProperties)
            is Uni2Properties -> Uni2Rows(hp = character.hp, umo = character.umo, props = character.characterProperties)
            is Roa2Properties -> Roa2Rows(props = character.characterProperties)
            else -> FallbackRows(hp = character.hp, umo = character.umo)
        }
    }
}

@Composable
private fun InfoCell(
    label: StringResource,
    value: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(label),
            style = nerdTypography.labelMedium,
            color = nerdColorPalette.textSecondary,
        )
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "-",
            style = nerdTypography.bodyMedium,
            color = nerdColorPalette.textPrimary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InfoGrid(
    cells: List<InfoCellData>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
        verticalArrangement = Arrangement.spacedBy(nerdDimensions.componentGap),
    ) {
        cells.forEach { cell ->
            InfoCell(
                label = cell.label,
                value = cell.value,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FallbackRows(
    hp: String?,
    umo: ImmutableList<String>,
) {
    val cells = buildList {
        if (hp != null) add(InfoCellData(Res.string.move_list_char_hp, hp))
        if (umo.isNotEmpty()) {
            add(InfoCellData(Res.string.move_list_char_umo, umo.joinToString(", ")))
        }
    }
    if (cells.isNotEmpty()) InfoGrid(cells = cells)
}

@Composable
private fun Sf6Rows(
    hp: String?,
    props: SF6Properties,
) {
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp_life_points, hp),
        InfoCellData(Res.string.move_list_char_fwd_walk_speed, props.fwdWalkSpd),
        InfoCellData(Res.string.move_list_char_bwd_walk_speed, props.bwdWalkSpd),
        InfoCellData(Res.string.move_list_char_fwd_dash_speed, props.fwdDashSpd),
        InfoCellData(Res.string.move_list_char_bwd_dash_speed, props.bwdDashSpd),
        InfoCellData(Res.string.move_list_char_fwd_dash_dist, props.fwdDashDist),
        InfoCellData(Res.string.move_list_char_bwd_dash_dist, props.bwdDashDist),
        InfoCellData(Res.string.move_list_char_drush_min_throw, props.dRushMin),
        InfoCellData(Res.string.move_list_char_drush_min_block, props.dRushBlock),
        InfoCellData(Res.string.move_list_char_drush_max, props.dRushMax),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun Mk1Rows(
    hp: String?,
    props: MK1Properties,
) {
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp, hp),
        InfoCellData(Res.string.move_list_char_hp_mod, props.hpMod),
        InfoCellData(Res.string.move_list_char_throw_dmg, props.throwDmg),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun GgstRows(
    umo: ImmutableList<String>,
    props: GGSTProperties,
) {
    val backdashValue = listOfNotNull(props.bwdDashDuration, props.bwdDashInvulnerability)
        .joinToString("\n")
        .ifBlank { null }
    val umoValue = umo.joinToString(", ").ifBlank { null }
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_dmg_received_mod, props.defense),
        InfoCellData(Res.string.move_list_char_guts, props.guts),
        InfoCellData(Res.string.move_list_char_backdash, backdashValue),
        InfoCellData(Res.string.move_list_char_dash_init_spd, props.dashInitialSpd),
        InfoCellData(Res.string.move_list_char_dash_accel, props.dashAcceleration),
        InfoCellData(Res.string.move_list_char_air_dash_dist, props.adDist),
        InfoCellData(Res.string.move_list_char_air_bwd_dash_dist, props.abdDist),
        InfoCellData(Res.string.move_list_char_jump_startup, props.prejump),
        InfoCellData(Res.string.move_list_char_umo, umoValue),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun BbcfRows(
    hp: String?,
    umo: ImmutableList<String>,
    props: BBProperties,
) {
    val umoValue = umo.joinToString(", ").ifBlank { null }
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp_health, hp),
        InfoCellData(Res.string.move_list_char_prejump, props.preJump),
        InfoCellData(Res.string.move_list_char_backdash, props.backDash),
        InfoCellData(Res.string.move_list_char_umo, umoValue),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun DbfzRows(
    hp: String?,
    props: DBFZProperties,
) {
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp, hp),
        InfoCellData(Res.string.move_list_char_ki_mod, props.kiMod),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun GbvsrRows(
    hp: String?,
    umo: ImmutableList<String>,
    props: GBVSRProperties,
) {
    val umoValue = umo.joinToString(", ").ifBlank { null }
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp, hp),
        InfoCellData(Res.string.move_list_char_backdash, props.backdash),
        InfoCellData(Res.string.move_list_char_umo, umoValue),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun MtfsRows(
    umo: ImmutableList<String>,
    props: MTFSProperties,
) {
    val umoValue = umo.joinToString(", ").ifBlank { null }
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_backdash, props.backdash),
        InfoCellData(Res.string.move_list_char_umo, umoValue),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun Uni2Rows(
    hp: String?,
    umo: ImmutableList<String>,
    props: Uni2Properties,
) {
    val backdashValue = listOfNotNull(props.bDashDuration, props.bDashDurationNote)
        .joinToString(" ")
        .ifBlank { null }
    val umoValue = umo.joinToString(", ").ifBlank { null }
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_hp_health, hp),
        InfoCellData(Res.string.move_list_char_prejump, props.jumpStartup),
        InfoCellData(Res.string.move_list_char_backdash, backdashValue),
        InfoCellData(Res.string.move_list_char_umo, umoValue),
    )
    InfoGrid(cells = cells)
}

@Composable
private fun Roa2Rows(
    props: Roa2Properties,
) {
    val cells = listOf(
        InfoCellData(Res.string.move_list_char_weight, props.weight),
        InfoCellData(Res.string.move_list_char_hitstun_gravity, props.hitstunGravity),
        InfoCellData(Res.string.move_list_char_max_fall_speed, props.fallSpeedMax),
        InfoCellData(Res.string.move_list_char_dash_speed, props.dashSpeed),
        InfoCellData(Res.string.move_list_char_dash_frames, props.dashFrames),
        InfoCellData(Res.string.move_list_char_max_run_speed, props.runSpeedMax),
        InfoCellData(Res.string.move_list_char_ground_friction, props.frictionGround),
        InfoCellData(Res.string.move_list_char_h_jump_speed, props.jumpSpeedHorizontalMax),
        InfoCellData(Res.string.move_list_char_air_speed, props.airSpeedHorizontalMax),
        InfoCellData(Res.string.move_list_char_air_accel, props.airAcceleration),
        InfoCellData(Res.string.move_list_char_short_hop_height, props.shortHopSpeed),
        InfoCellData(Res.string.move_list_char_full_hop_height, props.fullHopSpeed),
        InfoCellData(Res.string.move_list_char_double_jump_height, props.doubleJumpSpeed),
    )
    InfoGrid(cells = cells)
}
