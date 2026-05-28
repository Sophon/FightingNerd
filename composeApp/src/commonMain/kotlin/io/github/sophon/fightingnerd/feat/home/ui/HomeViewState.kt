package io.github.sophon.fightingnerd.feat.home.ui

import io.github.sophon.core.feature.Game

internal data class HomeViewState(
    val gameWidgetList: List<GameWidget> = emptyList(),

    val error: String? = null,
) {
    data class GameWidget(
        val game: Game,
        val featureName: String,
        val characterList: List<Character> = emptyList(),
        val isExpanded: Boolean = false,
        val isLoading: Boolean = true,
    ) {
        internal data class Character(
            val id: String,
            val displayName: String,
            val queryName: String,
            val iconUrl: String? = null,
        )
    }


    companion object {
        private fun mockCharacters(): List<GameWidget.Character> {
            val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
            return names.mapIndexed { index, name ->
                GameWidget.Character(
                    id = "char_$index",
                    displayName = name,
                    queryName = "",
                )
            }
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
            gameWidgetList = listOf(
                mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true, isLoading = false),
                mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false, isLoading = false),
                mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false, isLoading = false),
            )
        )
    }
}
