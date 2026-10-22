package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import kotlinx.serialization.Serializable

@Serializable
internal data class DragDownMoveResponseDto(
    val chara: String,
    val attack: String? = null,
    val attackID: String? = null,
    val mode: String? = null,
    val image: List<String>? = null,
    val hitbox: List<String>? = null,
    val caption: List<@Serializable(with = DragDownWikitextStringSerializer::class) String>? = null,
    val hitboxCaption: List<String>? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val startup: String? = null,
    val startupNotes: String? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val totalActive: String? = null,
    val totalActiveNotes: String? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val endlag: String? = null,
    val endlagNotes: String? = null,
    val cancel: List<String>? = null,
    val cancelNotes: List<String>? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val landingLag: String? = null,
    val landingLagNotes: String? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val iasa: String? = null,
    val iasaNotes: String? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val totalDuration: String? = null,
    val totalDurationNotes: String? = null,
    @Serializable(with = DragDownWikitextStringSerializer::class)
    val ledgeGrabFrame: String? = null,
    val ledgeGrabFrameNotes: String? = null,
    val frameChart: String? = null,
    val hitID: List<String>? = null,
    val hitMoveID: List<String>? = null,
    val hitName: List<@Serializable(with = DragDownWikitextStringSerializer::class) String>? = null,
    val hitActive: List<@Serializable(with = DragDownWikitextStringSerializer::class) String>? = null,
    val customShieldSafety: List<String>? = null,
    val uniqueField: List<String>? = null,
    val articleID: List<String>? = null,
    val notes: String? = null,
    val advNotes: String? = null,
)
