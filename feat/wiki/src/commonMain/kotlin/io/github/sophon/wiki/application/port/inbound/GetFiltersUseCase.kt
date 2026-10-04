package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.Filter
import io.github.sophon.wiki.application.domain.model.wiki.Game

interface GetFiltersUseCase {
    operator fun invoke(game: Game): Set<Filter>
}
