package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError

/**
 * Applies the host's [WikiConfig]. Games that were enabled before and aren't in the new config get their
 * characters and moves deleted. The first call has no previous config, so nothing gets deleted.
 *
 * The config is applied even if a delete fails - an error only means some data was left behind.
 */
interface ConfigureWikiUseCase {
    suspend operator fun invoke(wikiConfig: WikiConfig): EmptyResult<WikiError>
}
