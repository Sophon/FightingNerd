package io.github.sophon.xko.data

import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.wiki.domain.model.Character

@ExcludeFromCoverage("only from string")
internal fun String.toCharacter(): Character {
    return Character(
        id = this.lowercase(),
        displayName = this,
        queryName = this,
        wikiUrl = "https://wiki.play2xko.com/en-us/$this"
    )
}