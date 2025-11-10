package io.github.sophon.core.wiki.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String,
    val displayName: String,
    val queryName: String,
    val wikiUrl: String,
    val aliasList: List<String> = listOf(),
    val images: Images? = null,

    val sf6Properties: SF6Properties? = null,
) {
    @Serializable
    data class Images(
        val iconUrl: String? = null,
        val bannerUrl: String? = null,
    )

    @Serializable
    data class SF6Properties(
        val fwdWalkSpd: String,
        val bwdWalkSpd: String,
        val fwdDashSpd: String,
        val bwdDashSpd: String,
        val fwdDashDist: String,
        val bwdDashDist: String,

        val dRushMin: String,
        val dRushBlock: String,
        val dRushMax: String,

        val hp: String,
        val throwRange: String,
        val throwHurtbox: String,
        val jumpSpd: String,
        val jumpApex: String,
        val fwdJumpDist: String,
        val bwdJumpDist: String,
    )
}