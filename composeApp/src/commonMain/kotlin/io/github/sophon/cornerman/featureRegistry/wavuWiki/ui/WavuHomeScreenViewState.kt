package io.github.sophon.cornerman.featureRegistry.wavuWiki.ui

import io.github.sophon.wikiwavu.domain.model.Character

data class WavuHomeScreenViewState(
    val characterList: List<Character> = listOf(),
    val isExpanded: Boolean = true,

    val isLoading: Boolean = true,
    val error: String? = null,
) {
    companion object {
        internal val PREVIEW = WavuHomeScreenViewState(
            characterList = listOf(
                Character(
                    id = "",
                    displayName = "Alisa",
                    wikiUrl = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Nina",
                    wikiUrl = "",
                    aliasList = listOf(),
                    images = Character.Images(
                        url = "https://tekkendocs.com/t8/avatars/nina-brand-512.png",
                    )
                ),
                Character(
                    id = "lidia",
                    displayName = "Lidia",
                    wikiUrl = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Armor King",
                    wikiUrl = "",
                    aliasList = listOf(),
                    images = Character.Images(
                        url = "https://tekkendocs.com/t8/avatars/armor-king-brand-512.png"
                    )
                ),
                Character(
                    id = "",
                    displayName = "Panda",
                    wikiUrl = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Shaheen",
                    wikiUrl = "",
                    aliasList = listOf(),
                    images = Character.Images(
                        url = "https://tekkendocs.com/t8/avatars/shaheen-brand-512.png"
                    )
                ),
                Character(
                    id = "",
                    displayName = "Steve",
                    wikiUrl = "Steve",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Raven",
                    wikiUrl = "",
                    aliasList = listOf(),
                    images = Character.Images(
                        url = "https://tekkendocs.com/t8/avatars/raven-brand-512.png"
                    ),
                ),
                Character(
                    id = "",
                    displayName = "Yoshimitsu",
                    wikiUrl = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Paul",
                    wikiUrl = "",
                    aliasList = listOf(),
                    images = Character.Images(
                        url = "https://tekkendocs.com/t8/avatars/paul-brand-512.png"
                    )
                ),
            )
        )
    }
}
