package io.github.sophon.fightingnerd.feat.quiz.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Question(
    val characterName: String,
    val options: List<MoveOption>,
    val correctIndex: Int,
    val answeredIndex: Int? = null,
) {
    init {
        require(options.size == 4)
        require(correctIndex in options.indices)
    }


    val correct: MoveOption get() {
        return options[correctIndex]
    }

    val answered: MoveOption? get() {
        return answeredIndex?.let { options[it] }
    }

    data class MoveOption(
        val id: String,
        val input: String,
        val startup: String?,
        val onBlock: String?,
        val onHit: String?,
        val onCH: String? = null,

        val urls: Urls? = null,
    ) {
        data class Urls(
            val videoUrl: String? = null,
            val hitboxImageList: ImmutableList<String> = persistentListOf(),
            val moveImageList: ImmutableList<String> = persistentListOf(),
        )
    }
}
