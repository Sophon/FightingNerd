package io.github.sophon.fightingnerd.feat.quiz.model

import io.github.sophon.core.featureConfig.model.Game

internal data class QuizGameWidget(
    val game: Game,
    val featureName: String,
    val isReady: Boolean,
)