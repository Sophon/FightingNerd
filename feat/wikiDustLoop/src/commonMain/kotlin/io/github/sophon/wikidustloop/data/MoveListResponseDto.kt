package io.github.sophon.wikidustloop.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MoveListResponseDto(
    @SerialName("cargoquery") val cargoQuery: List<MoveQueryItem>,
)

@Serializable
internal data class MoveQueryItem(
    val title: MoveDto,
)

@Serializable
internal data class MoveDto(
    val chara: String? = null,
    val name: String? = null,
    val input: String? = null,
    val damage: String? = null,
    val guard: String? = null,
    val startup: String? = null,
    val active: String? = null,
    val recovery: String? = null,
    val onBlock: String? = null,
    val onHit: String? = null,
    val level: String? = null,
    val counter: String? = null,
    val images: String? = null,
    val hitboxes: String? = null,
    val notes: String? = null,
    val type: String? = null,
    val prorate: String? = null,
    val invuln: String? = null,
    val cancel: String? = null,
    val caption: String? = null,
    val hitboxCaption: String? = null,

    //gg
    val riscGain: String? = null,
    val riscLoss: String? = null,
    val wallDamage: String? = null,
    val inputTension: String? = null,
    val chipRatio: String? = null,
    val OTGType: String? = null,

    //db
    val attribute: String? = null,
    val smash: String? = null,
    val kigain: String? = null,
    val blockstun: String? = null,
    val groundHit: String? = null,
    val airHit: String? = null,

    //gbvs
    val onCH: String? = null,
    val meter: String? = null,
    val cooldown: String? = null,
    val cls: String? = null,

    //bb
    val onODR: String? = null,
    val p1: String? = null,
    val p2: String? = null,
    val starter: String? = null,
    val groundCH: String? = null,
    val airCH: String? = null,
    val blockstop: String? = null,
    val hitstop: String? = null,
    val CHstop: String? = null,
    val cancelTiming: String? = null,

    //mtfs
    val simpleInput: String? = null,
    val untechAmount: String? = null,
    val meterGain: String? = null,
)