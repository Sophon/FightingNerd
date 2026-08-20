package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.runtime.Immutable
import io.github.sophon.core.featureConfig.model.Game
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal data class HomeViewState(
    val gameWidgetList: ImmutableList<GameWidget> = persistentListOf(),

    val error: String? = null,
) {
    @Immutable
    data class GameWidget(
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
        ): GameWidget {
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


    companion object {
        private fun mockCharacters(): ImmutableList<GameWidget.UiCharacter> {
            val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
            val mocked = names.mapIndexed { index, name ->
                GameWidget.UiCharacter(
                    id = "char_$index",
                    displayName = name,
                    queryName = "",
                    hasMoves = true,
                )
            }.toImmutableList()
            return mocked
        }

        private fun mockWidget(
            game: Game,
            featureName: String,
            isExpanded: Boolean,
        ): GameWidget {
            val widget = GameWidget(
                game = game,
                featureName = featureName,
                characterList = mockCharacters(),
                isExpanded = isExpanded,
            )
            return widget
        }

        val PREVIEW = HomeViewState(
            gameWidgetList = persistentListOf(
                mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true),
                mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false),
                mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false),
            )
        )
    }
}
