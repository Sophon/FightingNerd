package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.Default
import io.github.sophon.wiki.model.Group
import io.github.sophon.wiki.model.wiki.Game

interface GetGroupsUseCase {
    operator fun invoke(
        game: Game,
        extras: List<String> = emptyList(),
    ): List<Group> = listOf(Default)
}
