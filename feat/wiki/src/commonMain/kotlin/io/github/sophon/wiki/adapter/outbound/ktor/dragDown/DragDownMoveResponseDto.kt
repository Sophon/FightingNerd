package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DragDownMoveResponseDto(
    @SerialName("chara") val chara: String,
    @SerialName("attack") val attack: String? = null,
    @SerialName("attack_id") val attackID: String? = null,
    @SerialName("mode") val mode: String? = null,
    @SerialName("image") val image: List<String>? = null,
    @SerialName("hitbox") val hitbox: List<String>? = null,
    @SerialName("caption") val caption: List<String>? = null,
    @SerialName("hitbox_caption") val hitboxCaption: List<String>? = null,
    @SerialName("startup") val startup: String? = null,
    @SerialName("startup_notes") val startupNotes: String? = null,
    @SerialName("total_active") val totalActive: String? = null,
    @SerialName("total_active_notes") val totalActiveNotes: String? = null,
    @SerialName("endlag") val endlag: String? = null,
    @SerialName("endlag_notes") val endlagNotes: String? = null,
    @SerialName("cancel") val cancel: List<String>? = null,
    @SerialName("cancel_notes") val cancelNotes: List<String>? = null,
    @SerialName("landing_lag") val landingLag: String? = null,
    @SerialName("landing_lag_notes") val landingLagNotes: String? = null,
    @SerialName("iasa") val iasa: String? = null,
    @SerialName("iasa_notes") val iasaNotes: String? = null,
    @SerialName("total_duration") val totalDuration: String? = null,
    @SerialName("total_duration_notes") val totalDurationNotes: String? = null,
    @SerialName("ledge_grab_frame") val ledgeGrabFrame: String? = null,
    @SerialName("ledge_grab_frame_notes") val ledgeGrabFrameNotes: String? = null,
    @SerialName("frame_chart") val frameChart: String? = null,
    @SerialName("hit_id") val hitID: List<String>? = null,
    @SerialName("hit_move_id") val hitMoveID: List<String>? = null,
    @SerialName("hit_name") val hitName: List<String>? = null,
    @SerialName("hit_active") val hitActive: List<String>? = null,
    @SerialName("custom_shield_safety") val customShieldSafety: List<String>? = null,
    @SerialName("unique_field") val uniqueField: List<String>? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("adv_notes") val advNotes: String? = null,
    @Serializable(with = DragDownBucketBooleanSerializer::class)
    @SerialName("no_frame_data") val noFrameData: Boolean = false,
)
