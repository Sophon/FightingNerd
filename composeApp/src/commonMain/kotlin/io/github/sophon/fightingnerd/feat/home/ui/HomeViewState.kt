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
        val characterList: ImmutableList<Character> = persistentListOf(),
        val isExpanded: Boolean = false,
        val isLoading: Boolean = true,
    ) {
        @Immutable
        internal data class Character(
            val id: String,
            val displayName: String,
            val queryName: String,
            val iconUrl: String? = null,
            val isLoading: Boolean = true,
        )

        fun withUpdatedCharacter(
            characterId: String,
        ): GameWidget {
            val updatedCharacterList = characterList.map { character ->
                if (character.id == characterId) {
                    character.copy(isLoading = false)
                } else {
                    character
                }
            }.toImmutableList()
            return copy(characterList = updatedCharacterList)
        }

        fun withUpdatedCharacters(
            characterIds: Collection<String>,
        ): GameWidget {
            val idSet = characterIds.toSet()
            val updatedCharacterList = characterList.map { character ->
                if (character.id in idSet) {
                    character.copy(isLoading = false)
                } else {
                    character
                }
            }.toImmutableList()
            return copy(characterList = updatedCharacterList)
        }
    }


    companion object {
        private fun mockCharacters(): ImmutableList<GameWidget.Character> {
            val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
            val mocked = names.mapIndexed { index, name ->
                GameWidget.Character(
                    id = "char_$index",
                    displayName = name,
                    queryName = "",
                )
            }.toImmutableList()
            return mocked
        }

        private fun mockWidget(
            game: Game,
            featureName: String,
            isExpanded: Boolean,
            isLoading: Boolean,
        ): GameWidget {
            return GameWidget(
                game = game,
                featureName = featureName,
                characterList = mockCharacters(),
                isExpanded = isExpanded,
                isLoading = isLoading,
            )
        }

        val PREVIEW = HomeViewState(
            gameWidgetList = persistentListOf(
                mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true, isLoading = false),
                mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false, isLoading = false),
                mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false, isLoading = false),
            )
        )
    }
}
