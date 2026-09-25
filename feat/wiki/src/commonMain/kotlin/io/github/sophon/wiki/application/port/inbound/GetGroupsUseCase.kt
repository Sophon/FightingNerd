package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wiki.application.domain.model.Default

interface GetGroupsUseCase {
    operator fun invoke(
        game: Game,
        extras: List<String> = emptyList(),
    ): List<Group> = listOf(Default)
}
