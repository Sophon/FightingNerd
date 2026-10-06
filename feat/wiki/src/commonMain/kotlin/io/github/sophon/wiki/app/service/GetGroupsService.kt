package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.inPort.GetGroupsUseCase
import io.github.sophon.wiki.model.Group
import io.github.sophon.wiki.model.wiki.Game

internal class GetGroupsService : GetGroupsUseCase {
    override fun invoke(
        game: Game,
        extras: List<String>,
    ): List<Group> {
        TODO("Not yet implemented")
    }
}
