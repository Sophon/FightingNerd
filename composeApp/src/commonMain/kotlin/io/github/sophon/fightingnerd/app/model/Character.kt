package io.github.sophon.fightingnerd.app.model

data class Character(
    val id: String,
    val displayName: String,
    val iconUrl: String? = null,

    val hp: String? = null,
    val umo: List<String> = listOf(),

    val gameProperties: CharacterGameProperties? = null,
)
