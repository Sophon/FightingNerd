package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.Default
import io.github.sophon.wiki.application.domain.model.Group
import io.github.sophon.wiki.application.domain.model.wiki.Game

interface GetGroupsUseCase {
    operator fun invoke(
        game: Game,
        extras: List<String> = emptyList(),
    ): List<Group> = listOf(Default)
}
