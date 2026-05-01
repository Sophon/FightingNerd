package io.github.sophon.wikiSuperCombo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MoveListResponseDto(
    @SerialName("cargoquery") val cargoQuery: List<Title>,
) {
    @Serializable
    data class Title(
        @SerialName("title") val title: MoveDto,
    )
}

@Serializable
internal data class MoveDto(
    val moveId: String,
    val moveType: String,
    val chara: String,
    val input: String,
    val name: String? = null,
    val images: String? = null,
    val hitboxes: String? = null,
    val damage: String? = null,
    val chip: String? = null,
    val dmgScaling: String? = null,
    val startup: String? = null,
    val active: String? = null,
    val recovery: String? = null,
    val total: String? = null,
    val guard: String? = null,
    val cancel: String? = null,
    val hitconfirm: String? = null,
    val hitAdv: String? = null,
    val blockAdv: String? = null,
    val punishAdv: String? = null,
    val perfParryAdv: String? = null,
    val DRcancelHit: String? = null,
    val DRcancelBlk: String? = null,
    val afterDRHit: String? = null,
    val afterDRBlk: String? = null,
    val hitstun: String? = null,
    val blockstun: String? = null,
    val hitstop: String? = null,
    val driveDmgBlk: String? = null,
    val driveDmgHit: String? = null,
    val driveGain: String? = null,
    val superGainHit: String? = null,
    val superGainBlk: String? = null,
    val invuln: String? = null,
    val armor: String? = null,
    val airborne: String? = null,
    val jugStart: String? = null,
    val jugIncrease: String? = null,
    val jugLimit: String? = null,
    val projSpeed: String? = null,
    val atkRange: String? = null,
    val notes: String? = null,

    val cost: String? = null,
    val flawlessBlockAdv: String? = null,
    val hitCancelAdv: String? = null,
    val blockCancelAdv: String? = null,
    val punish: String? = null,

    //AVL
    val chiDamage: String? = null,
    val onBlock: String? = null,
    val onHit: String? = null,
    val flow: String? = null,
    val properties: String? = null,
)