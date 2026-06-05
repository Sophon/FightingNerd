package io.github.sophon.core.wiki.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val charName: String,
    val id: String,
    val name: String? = null,

    val input: String,
    val damage: String? = null,
    val startup: String? = null,
    val onBlock: String? = null,
    val onHit: String? = null,
    val onCH: String? = null,
    val active: String? = null,
    val cancel: String? = null,
    val recovery: String? = null,
    val guard: String? = null,
    val invulnerability: String? = null,

    val notes: List<String> = listOf(),
    val aliases: List <String> = listOf(),

    val urls: Urls,

    val t8Properties: T8Properties? = null,
    val sf6Properties: SF6Properties? = null,
    val koF15Properties: KOF15Properties? = null,
    val cotwProperties: COTWProperties? = null,
    val ggstProperties: GGSTProperties? = null,
    val dbfzProperties: DBFZProperties? = null,
    val gbvsrProperties: GBVSRProperties? = null,
    val mkProperties: MKProperties? = null,
    val mbProperties: MBProperties? = null,
    val bbProperties: BBProperties? = null,
    val uni2Properties: Uni2Properties? = null,
    val vsavProperties: VSAVProperties? = null,
    val avlProperties: AVLProperties? = null,
) {
    @Serializable
    data class Urls(
        val characterWiki: String? = null,
        val characterImage: String? = null,
        val videoId: String? = null, //TODO: change to videoUrl
        val hitboxImageList: List<String> = listOf(),
        val moveImageList: List<String> = listOf(),
        val wikiUrl: String? = null,
    )

    @Serializable
    data class T8Properties(
        val isHeat: Boolean = false,
        val isHoming: Boolean = false,
        val stance: String? = null,
        val isPowerCrush: Boolean = false,
        val isHighCrush: Boolean = false,
        val isLowCrush: Boolean = false,
    )

    @Serializable
    data class SF6Properties(
        val type: Type? = null,
        val images: List<String>? = null,
        val chip: String? = null,
        val dmgScaling: String? = null,
        val total: String? = null,
        val hitConfirm: String? = null,
        val punishAdv: String? = null,
        val perfParryAdv: String? = null,
        val DRcOH: String? = null,
        val DRcOB: String? = null,
        val DROH: String? = null,
        val DROB: String? = null,
        val hitStun: String? = null,
        val blockStun: String? = null,
        val hitStop: String? = null,
        val driveDmgOnBlock: String? = null,
        val driveDmgOnHit: String? = null,
        val driveGain: String? = null,
        val superGainOnHit: String? = null,
        val superGainOnBlock: String? = null,
        val armor: String? = null,
        val airborne: String? = null,
        val jugStart: String? = null,
        val jugIncrease: String? = null,
        val jugLimit: String? = null,
        val projectileSpeed: String? = null,
        val attackRange: String? = null,
    ) {
        enum class Type {
            GROUND_NORMAL,
            AIR_NORMAL,
            SPECIAL,
            SUPER,
            THROW,
            DRIVE,
            TAUNT,
        }
    }

    @Serializable
    data class KOF15Properties(
        val stun: String? = null,
    )

    @Serializable
    data class COTWProperties(
        val revDamage: String? = null,
    )

    @Serializable
    data class GGSTProperties(
        val type: String? = null,
        val riscGain: String? = null,
        val riscLoss: String? = null,
        val wallDamage: String? = null,
        val inputTension: String? = null,
        val chipRatio: String? = null,
        val otgType: String? = null,
        val prorate: String? = null,
        val level: String? = null,
    )

    @Serializable
    data class DBFZProperties(
        val attribute: String? = null,
        val smash: String? = null,
        val kiGain: String? = null,
        val prorate: String? = null,
        val blockStun: String? = null,
        val groundHit: String? = null,
        val airHit: String? = null,
        val type: String? = null,
        val level: String? = null,
    )

    @Serializable
    data class GBVSRProperties(
        val meter: String? = null,
        val level: String? = null,
        val cooldown: String? = null,
        val cls: String? = null,
        val type: String? = null,
    )

    @Serializable
    data class MKProperties(
        val moveType: String? = null,
        val cost: List<String> = listOf(),
        val chip: String? = null,
        val flawlessBlockAdv: String? = null,
        val hitCancelAdv: String? = null,
        val blockCancelAdv: String? = null,
        val punish: String? = null,
    )

    @Serializable
    data class MBProperties(
        val inputInfo: String? = null,
        val subtitle: String? = null,
        val minDamage: String? = null,
        val property: String? = null,
        val cost: String? = null,
        val attribute: String? = null,
        val landing: String? = null,
        val overall: String? = null,
    )

    @Serializable
    data class BBProperties(
        val onODR: String? = null,
        val attribute: String? = null,
        val p1: String? = null,
        val p2: String? = null,
        val starter: String? = null,
        val level: String? = null,
        val blockstun: String? = null,
        val groundHit: String? = null,
        val airHit: String? = null,
        val groundCH: String? = null,
        val airCH: String? = null,
        val blockstop: String? = null,
        val hitstop: String? = null,
        val chStop: String? = null,
        val cancelTiming: String? = null,
        val type: String? = null
    )

    @Serializable
    data class Uni2Properties(
        val inputInfo: String? = null,
        val subtitle: String? = null,
        val minDamage: String? = null,
        val type: String? = null,
        val cancelWindow: String? = null,
        val property: String? = null,
        val cost: String? = null,
        val attribute: String? = null,
        val landing: String? = null,
        val overall: String? = null,
        val assaultAdv: String? = null,
        val blockstun: String? = null,
        val groundHit: String? = null,
        val airHit: String? = null,
        val groundCH: String? = null,
        val airCH: String? = null,
        val hitstop: String? = null,
        val CHstop: String? = null,
        val proration: String? = null,
        val comboP1: String? = null,
        val comboP2: String? = null,
    )

    @Serializable
    data class VSAVProperties(
        val inputInfo: String? = null,
        val subtitle: String? = null,
        val whiteDmg: String? = null,
        val renda: String? = null,
        val meter: String? = null,
        val reaction: String? = null,
        val curseTime: String? = null,
    )

    @Serializable
    data class AVLProperties(
        val chiDamage: String? = null,
        val flow: String? = null,
    )
}