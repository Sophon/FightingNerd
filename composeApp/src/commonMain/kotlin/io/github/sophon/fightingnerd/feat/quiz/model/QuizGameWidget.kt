package io.github.sophon.fightingnerd.feat.quiz.model

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.components.CharacterCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal data class QuizGameWidget(
    val game: Game,
    val featureName: String,
    val isReady: Boolean,
    val isPlayable: Boolean = false,
    val isExpanded: Boolean = false,
    val characterList: ImmutableList<CharacterCard> = persistentListOf(),
)
