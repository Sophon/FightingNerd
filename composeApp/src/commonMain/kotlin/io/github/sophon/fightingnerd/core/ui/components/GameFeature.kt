package io.github.sophon.fightingnerd.core.ui.components

import androidx.compose.runtime.Immutable
import io.github.sophon.core.featureConfig.model.Game
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class GameFeature(
    val game: Game,
    val featureName: String,
    val characterList: ImmutableList<UiCharacter> = persistentListOf(),
    val isExpanded: Boolean = false,
) {
    val isLoading: Boolean
        get() {
            val loading = characterList.isEmpty()
            return loading
        }

    @Immutable
    internal data class UiCharacter(
        val id: String,
        val displayName: String,
        val queryName: String,
        val iconUrl: String? = null,
        val hasMoves: Boolean = false,
    ) {
        val isLoading: Boolean
            get() {
                val loading = hasMoves.not()
                return loading
            }
    }

    fun withUpdatedCharacter(
        characterId: String,
    ): GameFeature {
        val updatedCharacterList = characterList.map { character ->
            if (character.id == characterId) {
                character.copy(hasMoves = true)
            } else {
                character
            }
        }.toImmutableList()
        val updated = copy(characterList = updatedCharacterList)
        return updated
    }
}
