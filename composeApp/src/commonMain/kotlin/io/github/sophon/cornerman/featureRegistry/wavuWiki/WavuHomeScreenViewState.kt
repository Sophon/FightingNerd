package io.github.sophon.cornerman.featureRegistry.wavuWiki

import io.github.sophon.wikiwavu.domain.model.Character

data class WavuHomeScreenViewState(
    val characterList: List<Character> = listOf(),

    val isLoading: Boolean = true,
    val error: String? = null,
) {
    companion object {
        internal val PREVIEW = WavuHomeScreenViewState(
            characterList = listOf(
                Character(
                    name = "Alisa",
                    alias = listOf(),
                ),
                Character(
                    name = "Nina",
                    alias = listOf(),
                    portraitUrl = "https://i.imgur.com/PAUToyh.png"
                ),
                Character(
                    name = "Lidia",
                    alias = listOf(),
                ),
                Character(
                    name = "Armor King",
                    alias = listOf(),
                    portraitUrl = "https://i.imgur.com/PAUToyh.png"
                ),
                Character(
                    name = "Panda",
                    alias = listOf(),
                ),
                Character(
                    name = "Shaheen",
                    alias = listOf(),
                    portraitUrl = "https://i.imgur.com/PAUToyh.png"
                ),
                Character(
                    name = "Steve",
                    alias = listOf(),
                ),
                Character(
                    name = "Raven",
                    alias = listOf(),
                    portraitUrl = "https://i.imgur.com/PAUToyh.png"
                ),
                Character(
                    name = "Yoshimitsu",
                    alias = listOf(),
                ),
                Character(
                    name = "Paul",
                    alias = listOf(),
                    portraitUrl = "https://i.imgur.com/PAUToyh.png"
                ),
            )
        )
    }
}
