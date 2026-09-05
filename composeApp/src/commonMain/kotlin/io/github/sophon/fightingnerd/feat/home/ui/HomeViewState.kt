package io.github.sophon.fightingnerd.feat.home.ui

import androidx.compose.runtime.Immutable
import io.github.sophon.core.featureConfig.model.Game
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class HomeViewState(
    val gameFeatureList: ImmutableList<GameFeature> = persistentListOf(),

    val error: String? = null,
) {
    companion object {
        private fun mockCharacters(): ImmutableList<GameFeature.UiCharacter> {
            val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
            val mocked = names.mapIndexed { index, name ->
                GameFeature.UiCharacter(
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
        ): GameFeature {
            val widget = GameFeature(
                game = game,
                featureName = featureName,
                characterList = mockCharacters(),
                isExpanded = isExpanded,
            )
            return widget
        }

        val PREVIEW = HomeViewState(
            gameFeatureList = persistentListOf(
                mockWidget(Game.Tekken8, "Wavu Wiki", isExpanded = true),
                mockWidget(Game.StreetFighter6, "SuperCombo", isExpanded = false),
                mockWidget(Game.KoFXV, "Dream Cancel", isExpanded = false),
            )
        )
    }
}
