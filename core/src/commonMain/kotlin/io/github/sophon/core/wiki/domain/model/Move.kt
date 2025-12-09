package io.github.sophon.core.wiki.domain.model

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
    val airDashProperties: AirDashProperties? = null,
) {
    @Serializable
    data class Urls(
        val characterWiki: String? = null,
        val videoId: String? = null, //TODO: change to videoUrl
        val hitboxImageList: List<String> = listOf(),
        val wikiUrl: String,
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
        val type: String? = null,
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
    data class AirDashProperties(
        val chara: String? = null,
        val name: String? = null,
        val input: String? = null,
        val damage: String? = null,
        val level: String? = null,
        val images: List<String>? = null,
        val hitboxes: List<String>? = null,
        val notes: List<String>? = null,
        val type: String? = null,
        val riscGain: String? = null,
        val riscLoss: String? = null,
        val wallDamage: String? = null,
        val inputTension: String? = null,
        val chipRatio: String? = null,
        val otgType: String? = null,
        val prorate: String? = null,
        val invuln: String? = null,
        val cancel: String? = null,
    )
}