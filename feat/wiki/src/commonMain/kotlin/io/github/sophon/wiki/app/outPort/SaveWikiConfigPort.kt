package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError

internal interface SaveWikiConfigPort {
    fun save(wikiConfig: WikiConfig): EmptyResult<WikiError>
}
