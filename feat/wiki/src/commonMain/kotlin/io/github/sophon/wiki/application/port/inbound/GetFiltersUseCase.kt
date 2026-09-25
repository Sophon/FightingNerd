package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Filter

interface GetFiltersUseCase {
    operator fun invoke(game: Game): Set<Filter>
}
