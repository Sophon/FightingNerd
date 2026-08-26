package io.github.sophon.core.wiki.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Move(
    val characterId: String,
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
    val isThrow: Boolean = false,

    //TODO: ideally, we'd have type here

    val notes: List<String> = listOf(),
    val aliases: List <String> = listOf(),

    val urls: Urls,

    val gameProperties: MoveGameProperties? = null,

    val koF15Properties: KOF15Properties? = null,
    val cotwProperties: COTWProperties? = null,
    val mbProperties: MBProperties? = null,
    val uni2Properties: Uni2Properties? = null,
    val vsavProperties: VSAVProperties? = null,
    val roa2Properties: Roa2Properties? = null,
) {
    @Serializable
    data class Urls(
        val wikiUrl: String, //this is mandatory so images embed as one comment
        val videoId: String? = null,
        val videoUrl: String? = null,
        val hitboxImageList: List<String> = listOf(),
        val moveImageList: List<String> = listOf(),
    )

    @Serializable
    data class KOF15Properties(
        val stun: String? = null,
    )

    @Serializable
    data class COTWProperties(
        val revDamage: String? = null,
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
    data class Roa2Properties(
        val mode: String? = null,
        val caption: List<String>? = null,
        val hitboxCaption: List<String>? = null,
        val startupNotes: String? = null,
        val totalActiveNotes: String? = null,
        val endlagNotes: String? = null,
        val cancelNotes: List<String>? = null,
        val landingLag: String? = null,
        val landingLagNotes: String? = null,
        val iasa: String? = null,
        val iasaNotes: String? = null,
        val totalDuration: String? = null,
        val totalDurationNotes: String? = null,
        val ledgeGrabFrame: String? = null,
        val ledgeGrabFrameNotes: String? = null,
        val hitID: List<String>? = null,
        val hitMoveID: List<String>? = null,
        val hitName: List<String>? = null,
        val hitActive: List<String>? = null,
        val customShieldSafety: List<String>? = null,
        val uniqueField: List<String>? = null,
        val articleID: List<String>? = null,
        val notes: String? = null,
        val advNotes: String? = null,
    )
}
