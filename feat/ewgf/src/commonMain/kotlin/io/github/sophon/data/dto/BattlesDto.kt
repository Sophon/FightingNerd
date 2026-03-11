package io.github.sophon.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
internal data class BattlesDto(
    @SerialName("_metadata") val metadata: Metadata,
    @SerialName("data") val data: List<Battle>,
)

@Serializable
internal data class Metadata(
    @SerialName("rate_limit_remaining") val rateLimitRemaining: Int,
    @SerialName("rate_limit_reset") val rateLimitReset: String,
    @SerialName("tier") val tier: String
)

@Serializable
internal data class Battle(
    @SerialName("battle_at") val battleAt: String,
    @SerialName("battle_type") val battleType: String,
    @SerialName("game_version") val gameVersion: Int,
    @SerialName("winner") val winner: Int,
    @SerialName("stage_id") val stageId: Int,
    @SerialName("p1_name") val p1Name: String,
    @SerialName("p1_tekken_id") val p1TekkenId: String,
    @SerialName("p1_char") val p1Char: String,
    @SerialName("p1_region") val p1Region: String,
    @SerialName("p1_tekken_power") val p1TekkenPower: Int,
    @SerialName("p1_dan_rank") val p1DanRank: String,
    @SerialName("p1_rounds_won") val p1RoundsWon: Int,
    @SerialName("p2_name") val p2Name: String,
    @SerialName("p2_tekken_id") val p2TekkenId: String,
    @SerialName("p2_char") val p2Char: String,
    @SerialName("p2_region") val p2Region: String,
    @SerialName("p2_dan_rank") val p2DanRank: String,
    @SerialName("p2_tekken_power") val p2TekkenPower: Int,
    @SerialName("p2_rounds_won") val p2RoundsWon: Int
)
