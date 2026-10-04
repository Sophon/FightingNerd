package io.github.sophon.wiki.application.domain.service

import io.github.sophon.wiki.application.domain.model.Filter
import io.github.sophon.wiki.application.domain.model.wiki.Game
import io.github.sophon.wiki.application.port.inbound.GetFiltersUseCase

internal class GetFiltersService : GetFiltersUseCase {
    override fun invoke(game: Game): Set<Filter> {
        TODO("Not yet implemented")
    }
}
