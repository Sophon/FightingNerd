package io.github.sophon.fightingnerd.adapter.inbound.quiz.model

import androidx.compose.runtime.Immutable
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.components.CharacterCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class QuizGameWidget(
    val game: Game,
    val isExpanded: Boolean = false,
    val characterList: ImmutableList<CharacterCard> = persistentListOf(),
) {
    val isReady: Boolean
        get() {
            val ready = characterList.isNotEmpty()
            return ready
        }

    val isPlayable: Boolean
        get() {
            val playable = isReady && characterList.none { it.isLoading }
            return playable
        }

    fun withPlayableCharacter(
        characterId: String,
    ): QuizGameWidget {
        val updatedCharacterList = characterList.map { character ->
            if (character.id == characterId) {
                character.copy(isLoading = false)
            } else {
                character
            }
        }.toImmutableList()
        val updated = copy(characterList = updatedCharacterList)
        return updated
    }
}
