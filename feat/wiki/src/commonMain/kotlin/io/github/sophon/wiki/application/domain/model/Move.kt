package io.github.sophon.wiki.application.domain.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
data class Move(
    val characterId: CharacterId,
    val id: MoveId,
    val input: String,

    val name: String? = null,
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

    val type: String? = null,

    val notes: List<String> = listOf(),
    val aliases: List <String> = listOf(),

    val urls: Urls,

    val gameProperties: MoveGameProperties? = null,
) {
    @Serializable
    data class Urls(
        val wikiUrl: String, //this is mandatory so images embed as one comment
        val videoId: String? = null,
        val videoUrl: String? = null,
        val hitboxImageList: List<String> = listOf(),
        val moveImageList: List<String> = listOf(),
    )
}

@JvmInline
@Serializable
value class MoveId(val value: String)
