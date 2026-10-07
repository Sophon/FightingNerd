package io.github.sophon.fightingnerd.adapter.inbound.home

import androidx.compose.runtime.Immutable
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Immutable
internal data class HomeViewState(
    val gameFeatureList: ImmutableList<GameFeature> = persistentListOf(),

    val error: String? = null,
) {
    val isAnyGameExpanded: Boolean get() {
        val expanded = gameFeatureList.any { it.isExpanded }
        return expanded
    }

    companion object {
        private fun mockCharacters(): ImmutableList<GameFeature.UiCharacter> {
            val names = listOf("Zuzana", "Eva", "Karolina", "Marcela", "Zdenka", "Hana")
            val mocked = names.mapIndexed { index, name ->
                GameFeature.UiCharacter(
                    id = "char_$index",
                    displayName = name,
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
                mockWidget(
                    game = Game(
                        id = "Tekken_8",
                        displayName = "Tekken 8",
                        iconUrl = "https://i.imgur.com/Yl6j809.png",
                        wiki = Wiki(name = "Wavu Wiki", url = "https://wavu.wiki/", iconUrl = "https://wavu.wiki/android-chrome-512x512.png"),
                    ),
                    featureName = "Wavu Wiki",
                    isExpanded = true,
                ),
                mockWidget(
                    game = Game(
                        id = "Street_Fighter_6",
                        displayName = "Street Fighter 6",
                        iconUrl = "https://i.imgur.com/N9wYA5K.png",
                        wiki = Wiki(name = "SuperCombo Wiki", url = "https://wiki.supercombo.gg/", iconUrl = "https://wiki.supercombo.gg/srk_wordmark.png"),
                    ),
                    featureName = "SuperCombo",
                    isExpanded = false,
                ),
                mockWidget(
                    game = Game(
                        id = "The_King_of_Fighters_XV",
                        displayName = "The King of Fighters XV",
                        iconUrl = "https://i.imgur.com/Zlin7xi.png",
                        wiki = Wiki(name = "DreamCancel Wiki", url = "https://dreamcancel.com/wiki", iconUrl = "https://dreamcancel.com/w/images/dclogooutlined2.png"),
                    ),
                    featureName = "Dream Cancel",
                    isExpanded = false,
                ),
            )
        )
    }
}
