package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError

internal interface SaveWikiConfigPort {
    fun save(wikiConfig: WikiConfig): EmptyResult<WikiError>
}
