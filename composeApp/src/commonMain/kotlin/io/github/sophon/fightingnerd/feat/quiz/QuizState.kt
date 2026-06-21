package io.github.sophon.fightingnerd.feat.quiz

import io.github.sophon.core.featureConfig.model.Game

internal data class QuizState(
    val gameList: List<Game> = emptyList(),
)
