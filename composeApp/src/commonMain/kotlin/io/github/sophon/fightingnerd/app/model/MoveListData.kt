package io.github.sophon.fightingnerd.app.model

data class MoveListData(
    val character: Character,
    val moveList: List<Move>,
    val game: Game,
)
