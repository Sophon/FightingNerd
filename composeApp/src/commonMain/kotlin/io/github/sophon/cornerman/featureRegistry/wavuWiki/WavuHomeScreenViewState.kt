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
                    id = "",
                    displayName = "Alisa",
                    wikiName = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Nina",
                    wikiName = "",
                    aliasList = listOf(),
                    image = Character.Image(
                        url = "https://tekkendocs.com/t8/avatars/nina-brand-512.png",
                    )
                ),
                Character(
                    id = "lidia",
                    displayName = "Lidia",
                    wikiName = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Armor King",
                    wikiName = "",
                    aliasList = listOf(),
                    image = Character.Image(
                        url = "https://tekkendocs.com/t8/avatars/armor-king-brand-512.png"
                    )
                ),
                Character(
                    id = "",
                    displayName = "Panda",
                    wikiName = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Shaheen",
                    wikiName = "",
                    aliasList = listOf(),
                    image = Character.Image(
                        url = "https://tekkendocs.com/t8/avatars/shaheen-brand-512.png"
                    )
                ),
                Character(
                    id = "",
                    displayName = "Steve",
                    wikiName = "Steve",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Raven",
                    wikiName = "",
                    aliasList = listOf(),
                    image = Character.Image(
                        url = "https://tekkendocs.com/t8/avatars/raven-brand-512.png"
                    ),
                ),
                Character(
                    id = "",
                    displayName = "Yoshimitsu",
                    wikiName = "",
                    aliasList = listOf(),
                ),
                Character(
                    id = "",
                    displayName = "Paul",
                    wikiName = "",
                    aliasList = listOf(),
                    image = Character.Image(
                        url = "https://tekkendocs.com/t8/avatars/paul-brand-512.png"
                    )
                ),
            )
        )
    }
}
