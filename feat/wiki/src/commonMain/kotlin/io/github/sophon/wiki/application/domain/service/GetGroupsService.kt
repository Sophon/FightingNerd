package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wiki.application.port.inbound.GetGroupsUseCase

internal class GetGroupsService : GetGroupsUseCase {
    override fun invoke(
        game: Game,
        extras: List<String>,
    ): List<Group> {
        TODO("Not yet implemented")
    }
}
