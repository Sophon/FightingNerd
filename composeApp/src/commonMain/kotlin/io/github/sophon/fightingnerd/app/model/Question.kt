package io.github.sophon.fightingnerd.app.model

data class Question(
    val character: Character,
    val options: List<Move>,
    val correctIndex: Int,
) {
    init {
        require(options.size == (COUNT_DISTRACTIONS + 1))
        require(correctIndex in options.indices)
    }
}


internal const val COUNT_QUESTIONS = 10
internal const val COUNT_DISTRACTIONS = 3 //4 questions -> 3 distractions; 6 questions -> 5 distractions etc
