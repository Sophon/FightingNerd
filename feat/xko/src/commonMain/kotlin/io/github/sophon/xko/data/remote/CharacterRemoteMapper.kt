package io.github.sophon.xko.data.remote

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.wiki.model.Character

@ExcludeFromCoverage("only from string")
internal fun String.toCharacter(): Character {
    return Character(
        id = this.lowercase(),
        displayName = this,
        remoteQueryId = this,
        wikiUrl = "https://wiki.play2xko.com/en-us/$this"
    )
}