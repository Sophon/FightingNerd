package io.github.sophon.fightingnerd.app.model

data class Move(
    val input: String,
    val remoteId: String? = null,

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
    val aliases: List<String> = listOf(),

    val urls: Urls,

    val gameProperties: MoveGameProperties? = null,

    val groupId: String,
    val filterNameSet: Set<String> = setOf(),
) {
    fun matches(searchQuery: String?): Boolean {
        if (searchQuery == null) return true

        val matches = input.contains(searchQuery, ignoreCase = true)
                || name.orEmpty().contains(searchQuery, ignoreCase = true)
                || aliases.any { alias -> alias.contains(searchQuery, ignoreCase = true) }
        return matches
    }

    data class Urls(
        val wikiUrl: String,
        val videoId: String? = null,
        val videoUrl: String? = null,
        val hitboxImageList: List<String> = listOf(),
        val moveImageList: List<String> = listOf(),
    ) {
        val mediaCount: Int
            get() {
                val count = (listOfNotNull(videoUrl).size + hitboxImageList.size + moveImageList.size)
                return count
            }
    }
}
